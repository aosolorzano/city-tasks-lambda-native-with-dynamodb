#!/usr/bin/env bash

cd "$WORKING_DIR"/utils/certs || {
  echo "Error moving to the TLS-Certs directory."
  exit 1
}
CERTS_DIR="$WORKING_DIR"/utils/certs

### CREATE CA CERTIFICATE
echo ""
if [ ! -f "$CERTS_DIR"/ca-key.pem ] || [ ! -f "$CERTS_DIR"/ca-cert.pem ]; then
  rm -f ./*.pem
  read -r -p 'Enter the <Domain Name> for your CA (Intermediate) certificate: ' ca_domain_name
  if [ -z "$ca_domain_name" ]; then
    echo "Error: You must enter a valid <Domain Name> for your CA certificate."
    exit 1
  fi
  openssl ecparam                 \
    -name prime256v1              \
    -genkey                       \
    -out ca-key.pem               \
    -outform PEM
  openssl req -new -x509 -sha256  \
    -key ca-key.pem               \
    -out ca-cert.pem              \
    -days 365                     \
    -subj "/C=EC/ST=Pichincha/L=UIO/O=Hiperium Company/OU=Innovation/CN=$ca_domain_name/emailAddress=support@$ca_domain_name"
fi

### CREATE CSR CERTIFICATE
if [ ! -f "$CERTS_DIR"/server-key.pem ] || [ ! -f "$CERTS_DIR"/server-cert.pem ]; then
  rm -f server-key-no-header.pem
  read -r -p 'Enter the <Domain Name> for your CSR (Server) certificate: ' server_domain_name
  if [ -z "$server_domain_name" ]; then
    server_domain_name='example.io'
  fi
  openssl ecparam                 \
    -name prime256v1              \
    -genkey                       \
    -out server-key.pem           \
    -outform PEM
  openssl req -new -sha256        \
    -key server-key.pem           \
    -out server-cert.pem          \
    -days 365                     \
    -subj "/C=EC/ST=Pichincha/L=UIO/O=Hiperium Cloud/OU=Engineering/CN=$server_domain_name/emailAddress=support@$server_domain_name"
    ### REMOVING HEADER FROM CSR PRIVATE KEY
    openssl ec -in server-key.pem -outform PEM -out server-key-no-header.pem
fi

echo ""
echo "Signing CSR certificate using CA certificate..."
echo ""
echo "subjectAltName = DNS:$AWS_WORKLOADS_ENV.$server_domain_name" > v3.ext
openssl x509 -req -sha256       \
  -in      server-cert.pem      \
  -CA      ca-cert.pem          \
  -CAkey   ca-key.pem           \
  -days    365                  \
  -extfile v3.ext               \
  -out     server-cert-"$AWS_WORKLOADS_ENV".pem   \
  -CAcreateserial

### CREATING WORKLOAD ENVIRONMENT DIRECTORY
mkdir -p "$WORKING_DIR"/utils/certs/"$AWS_WORKLOADS_ENV"

### MOVING CERTIFICATE FILES TO THE CORRESPONDING DIRECTORY
cp ca-cert.pem "$WORKING_DIR"/utils/certs
cp server-key-no-header.pem "$WORKING_DIR"/utils/certs/"$AWS_WORKLOADS_ENV"/server-key.pem
mv server-cert-"$AWS_WORKLOADS_ENV".pem ca-cert.srl "$WORKING_DIR"/utils/certs/"$AWS_WORKLOADS_ENV"
echo ""
echo "DONE!"
