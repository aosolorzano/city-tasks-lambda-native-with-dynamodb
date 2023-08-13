#!/bin/bash

WORKING_DIR=$(cd -P -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)
export WORKING_DIR

function setEnvironmentVariables() {
  echo ""
  read -r -p 'Enter the <AWS Profile> for the Service Workloads:     [default] ' aws_workloads_profile
  if [ -z "$aws_workloads_profile" ]; then
    AWS_WORKLOADS_PROFILE='default'
  else
    AWS_WORKLOADS_PROFILE=$aws_workloads_profile
  fi
  export AWS_WORKLOADS_PROFILE

  if [ -z "$AWS_PROFILE" ]; then
    read -r -p 'Enter the <AWS Profile> for the Deployment Tools:      [default] ' aws_profile
    if [ -z "$aws_profile" ]; then
      AWS_PROFILE='default'
    else
      AWS_PROFILE=$aws_profile
    fi
    export AWS_PROFILE
  fi

  if [ -z "$AWS_IDP_PROFILE" ]; then
    read -r -p 'Enter the <AWS Profile> where Cognito IdP is deployed: [default] ' idp_profile_name
    if [ -z "$idp_profile_name" ]; then
      AWS_IDP_PROFILE='idp-pre'
    else
      AWS_IDP_PROFILE=$idp_profile_name
    fi
    export AWS_IDP_PROFILE
  fi

  if [ -z "$AWS_WORKLOADS_ENV" ]; then
    read -r -p 'Enter the <AWS Environment> to deploy the Service:     [dev] ' env_name
    if [ -z "$env_name" ]; then
      AWS_WORKLOADS_ENV='dev'
    else
      AWS_WORKLOADS_ENV=$env_name
    fi
    export AWS_WORKLOADS_ENV
  fi
}

function verifyEnvironmentVariables() {
  if [ -z "$AWS_PROFILE" ] || [ -z "$AWS_WORKLOADS_PROFILE" ] || [ -z "$AWS_WORKLOADS_ENV" ] || [ -z "$AWS_IDP_PROFILE" ]; then
    clear
    setEnvironmentVariables
    clear
  fi
}

helperMenu() {
  echo "
    *********************************************
    **************** Helper Menu ****************
    *********************************************
    1) Revert Automated Files.
    2) Prune Docker System.
    3) Create Self-Signed Certificate.
    4) Import Self-Signed Certificate into ACM.
    5) Delete Self-Signed Certificate from ACM.
    6) Create Record-Set in Route53.
    7) Update Record-Set in Route53.
    8) Delete Record-Set from Route53.
    ---------------------------------------------
    r) Return.
    q) Quit.
  "
  read -r -p 'Choose an option: ' option
  case $option in
  1)
    clear
    echo ""
    echo "REVERTING CONFIGURATION FILES..."
    sh "$WORKING_DIR"/utils/scripts/helper/1_revert-automated-scripts.sh
    echo "DONE!"
    helperMenu
    ;;
  2)
    clear
    sh "$WORKING_DIR"/utils/scripts/helper/2_docker-system-prune.sh
    helperMenu
    ;;
  3)
    clear
    sh "$WORKING_DIR"/utils/scripts/helper/3_create-tls-certificate.sh
    helperMenu
    ;;
  4)
    clear
    sh "$WORKING_DIR"/utils/scripts/helper/4_import-tls-certificate-to-acm.sh
    helperMenu
    ;;
  5)
    clear
    sh "$WORKING_DIR"/utils/scripts/helper/5_delete-tls-certificate-from-acm.sh
    helperMenu
    ;;
  6)
    clear
    export UPDATE_RECORD_SET="false"
    sh "$WORKING_DIR"/utils/scripts/helper/6_7_register-alb-domain-in-route53.sh
    helperMenu
    ;;
  7)
    clear
    export UPDATE_RECORD_SET="true"
    sh "$WORKING_DIR"/utils/scripts/helper/6_7_register-alb-domain-in-route53.sh
    helperMenu
    ;;
  8)
    clear
    sh "$WORKING_DIR"/utils/scripts/helper/8_delete-alb-domain-from-route53.sh
    helperMenu
    ;;
  [Rr])
    clear
    menu
    ;;
  [Qq])
    clear
    echo ""
    echo "Done!"
    echo ""
    exit 0
    ;;
  *)
    clear
    echo -e 'Wrong option.'
    helperMenu
    ;;
  esac
}

menu() {
  echo "
    *************************************
    ************* Main Menu *************
    *************************************
    1) Docker Compose.
    2) Deploy Backend into AWS.
    3) Delete Backend from AWS.
    -------------------------------------
    h) Helper menu.
    q) Quit.
  "
  read -r -p 'Choose an option: ' option
  case $option in
  [Hh])
    clear
    helperMenu
    ;;
  1)
    clear
    sh "$WORKING_DIR"/utils/scripts/1_deploy-docker-cluster.sh
    menu
    ;;
  2)
    clear
    sh "$WORKING_DIR"/utils/scripts/2_create-backend.sh
    menu
    ;;
  3)
    sh "$WORKING_DIR"/utils/scripts/3_delete-backend.sh
    menu
    ;;
  [Qq])
    clear
    echo ""
    echo "Done!"
    echo ""
    exit 0
    ;;
  *)
    clear
    echo -e 'Wrong option.'
    menu
    ;;
  esac
}

#### Main function ####
verifyEnvironmentVariables
menu

