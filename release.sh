#!/bin/bash

# Release Automation Script for Maven Central Deployment
# This script automates the two-step release process

set -e  # Exit on any error

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
DEFAULT_BRANCH="main"

# Function to print colored output
print_status() {
  echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
  echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
  echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
  echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check prerequisites
check_prerequisites() {
  print_status "Checking prerequisites..."
  
  # Check if Maven is installed
  if ! command -v mvn &> /dev/null; then
    print_error "Maven is not installed or not in PATH"
    exit 1
  fi
  
  # Check if GPG is installed
  if ! command -v gpg &> /dev/null; then
    print_error "GPG is not installed or not in PATH"
    exit 1
  fi
  
  # Check if Git is installed
  if ! command -v git &> /dev/null; then
    print_error "Git is not installed or not in PATH"
    exit 1
  fi
  
  # Check if we're in a Git repository
  if ! git rev-parse --git-dir &> /dev/null; then
    print_error "Not in a Git repository"
    exit 1
  fi
  
  # Check if working directory is clean
  if ! git diff-index --quiet HEAD --; then
    print_error "Working directory is not clean. Please commit or stash your changes."
    exit 1
  fi
  
  # Check if on main/master branch
  CURRENT_BRANCH=$(git branch --show-current)
  if [[ "$CURRENT_BRANCH" != "$DEFAULT_BRANCH" && "$CURRENT_BRANCH" != "master" ]]; then
    print_warning "Not on main branch. Current branch: $CURRENT_BRANCH"
    read -p "Continue? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
      exit 1
    fi
  fi
  
  print_success "Prerequisites check passed"
}

# Function to display current project status
show_status() {
  print_status "Current project status:"
  
  # Show current version
  CURRENT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
  echo "  Current version: $CURRENT_VERSION"
  
  # Show current branch
  CURRENT_BRANCH=$(git branch --show-current)
  echo "  Current branch: $CURRENT_BRANCH"
  
  # Show Git status
  echo "  Git status:"
  if git diff-index --quiet HEAD --; then
    echo "    ✓ Working directory clean"
  else
    echo "    ⚠ Working directory has uncommitted changes"
  fi
  
  # Show remote configuration
  echo "  Git remote:"
  git remote -v | sed 's/^/    /'
  
  # Show latest commits
  echo "  Recent commits:"
  git log --oneline -5 | sed 's/^/    /'
  
  # Show existing tags
  echo "  Recent tags:"
  git tag -l | tail -5 | sed 's/^/    /' || echo "    (no tags found)"
  
  # Show GPG keys
  echo "  Available GPG keys:"
  gpg --list-secret-keys --keyid-format LONG | grep -E "(sec|uid)" | sed 's/^/    /'
  
  echo
}

# Function to run tests
run_tests() {
  print_status "Running tests..."
  mvn clean test
  print_success "Tests passed"
}

# Function to prepare release
prepare_release() {
  
  CURRENT_ARTIFACT=$(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout)

  print_status "Preparing release for ${CURRENT_ARTIFACT} ..."

  rm -f pom.xml.releaseBackup # Clean up any previous release backup
  rm -f release.properties # Clean up any previous release properties

  # Ask for release version
  CURRENT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
  SUGGESTED_RELEASE_VERSION=${CURRENT_VERSION%-SNAPSHOT}
  
  read -p "Release version [$SUGGESTED_RELEASE_VERSION]: " RELEASE_VERSION
  RELEASE_VERSION=${RELEASE_VERSION:-$SUGGESTED_RELEASE_VERSION}
  
  # Ask for next development version
  IFS='.' read -ra VERSION_PARTS <<< "$RELEASE_VERSION"
  NEXT_PATCH=$((${VERSION_PARTS[2]} + 1))
  SUGGESTED_NEXT_VERSION="${VERSION_PARTS[0]}.${VERSION_PARTS[1]}.$NEXT_PATCH-SNAPSHOT"
  
  read -p "Next development version [$SUGGESTED_NEXT_VERSION]: " NEXT_VERSION
  NEXT_VERSION=${NEXT_VERSION:-$SUGGESTED_NEXT_VERSION}
  
  print_status "Release version: $RELEASE_VERSION"
  print_status "Next development version: $NEXT_VERSION"
  
  # Confirm
  read -p "Proceed with release preparation? (y/N): " -n 1 -r
  echo
  if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    print_warning "Release preparation cancelled"
    exit 0
  fi

  gpg -ab pom.xml
  if [[ $? -ne 0 ]]; then
    print_error "Failed to sign using gpg! Ensure GPG is set up correctly."
    exit 1
  fi
  rm -f pom.xml.asc

  while true; do
    read -p "Repeat GPG Passphrase for signing: " MAVEN_GPG_PASSPHRASE
    read -p "OK to use : \"$MAVEN_GPG_PASSPHRASE\"? (y/N): " ok
    if [[ "$ok" == "y" ]]; then
      break
    fi
  done
  export MAVEN_GPG_PASSPHRASE

  # Run maven release:prepare
  print_status "Running maven release:prepare..."
  mvn release:prepare \
    -Dgpg.skip=false \
    -DreleaseVersion="$RELEASE_VERSION" \
    -DdevelopmentVersion="$NEXT_VERSION" \
    -Dtag="v${RELEASE_VERSION}"
  
  print_success "Release preparation completed"
}

# Function to perform release (Step 1)
perform_release() {
  print_status "Performing release (Step 1: Deploy to staging)..."

  # Run maven release:perform
  mvn -Dgpg.skip=false release:perform
  
  print_success "Release performed - artifacts deployed to OSSRH staging repository"
}

# Function to manage deployments at Central Portal (Step 2)
manage_staging() {
  print_status "Managing deployments at Central Portal (Step 2: Promote to Maven Central)..."

  echo
  print_warning "Please review the deployments in Maven Central Repository:"
  print_warning "https://central.sonatype.com/publishing/deployments"
  echo
  
  # Ask what to do
  echo "What would you like to do with the deployments?"
  echo "1) Close and release (promote to Maven Central)"
  echo "2) Close only (validate but don't release yet)"
  echo "3) Drop (delete the staging repository)"
  echo "4) Exit"
  
  while true; do
    read -p "Choose an option (1-5): " choice
    case $choice in
      1)
          print_status "Closing and releasing staging repository..."
          mvn nexus-staging:close
          mvn nexus-staging:release
          print_success "Artifacts released to Maven Central!"
          print_status "Artifacts will be available in ~10 minutes, searchable in ~2 hours"
          break
          ;;
      2)
          print_status "Closing staging repository..."
          mvn nexus-staging:close
          print_success "Staging repository closed and validated"
          print_status "You can release it later with: mvn nexus-staging:release"
          break
          ;;
      3)
          print_warning "Dropping staging repository..."
          read -p "Are you sure? This cannot be undone (y/N): " -n 1 -r
          echo
          if [[ $REPLY =~ ^[Yy]$ ]]; then
              mvn nexus-staging:drop
              print_success "Staging repository dropped"
          fi
          break
          ;;
      4)
          print_status "Exiting..."
          exit 0
          ;;
      *)
          print_error "Invalid option. Please choose 1-5."
          ;;
    esac
  done
}

# Function to clean up after failed release
cleanup_failed_release() {
  print_warning "Cleaning up failed release..."
  
  # Clean up release plugin files
  mvn release:clean
  
  # Reset to HEAD if needed
  if git tag | grep -q "v.*"; then
    LATEST_TAG=$(git tag | sort -V | tail -1)
    print_status "Latest tag: $LATEST_TAG"
    read -p "Delete tag $LATEST_TAG? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
      git tag -d "$LATEST_TAG"
      git push origin ":refs/tags/$LATEST_TAG"
    fi
  fi
  
  print_success "Cleanup completed"
}

# Function to show help
show_help() {
  cat << EOF
USAGE:
  $0 [OPTION]

OPTIONS:
  help, -h, --help    Show this help message
  status              Show current project status
  test                Run tests only
  prepare             Prepare release (Step 1a)
  perform             Perform release (Step 1b)
  staging             Manage staging repository (Step 2)
  full                Run complete release process (prepare + perform + staging)
  cleanup             Clean up failed release

EXAMPLES:
  $0 status           # Show project status
  $0 full             # Complete release process
  $0 prepare          # Just prepare the release
  $0 perform          # Perform the release when it is prepared
  $0 staging          # Just manage staging repository

RELEASE PROCESS:
  1. Prepare: Update versions, create tag, commit changes
  2. Perform: Build and deploy artifacts to OSSRH staging
  3. Staging: Close/validate and promote to Maven Central

For more information, see the README.md file.
EOF
}

# Main function
main() {
  echo "=========================================="
  echo "  Maven Central Release Script"
  echo "=========================================="
  echo
  
  case "${1:-full}" in
    "help"|"-h"|"--help")
      show_help
      ;;
    "status")
      check_prerequisites
      show_status
      ;;
    "test")
      check_prerequisites
      run_tests
      ;;
    "prepare")
      check_prerequisites
      show_status
      run_tests
      prepare_release
      ;;
    "perform")
      check_prerequisites
      perform_release
      ;;
    "staging")
      manage_staging
      ;;
    "full")
      check_prerequisites
      show_status
      run_tests
      prepare_release
      perform_release
      manage_staging
      ;;
    "cleanup")
      cleanup_failed_release
      ;;
    *)
      print_error "Unknown option: $1"
      show_help
      exit 1
      ;;
  esac
}

# Run main function with all arguments
main "$@"
