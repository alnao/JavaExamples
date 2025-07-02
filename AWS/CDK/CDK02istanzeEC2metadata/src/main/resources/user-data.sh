#!/bin/bash -xe

# Variabili che verranno sostituite dal codice Java
echo "TEST VpcId=${VPC_ID} SubnetId=${SUBNET_ID} stack=${STACK_NAME} region=${REGION}" > /tmp/test.txt

# Aggiorna cfn-bootstrap
yum update -y aws-cfn-bootstrap

# Esegui cfn-init
/opt/aws/bin/cfn-init -v --stack ${STACK_NAME} --resource EC2Instance --region ${REGION}
INIT_STATUS=$?

# Segnala il completamento
/opt/aws/bin/cfn-signal -e $INIT_STATUS '${WAIT_CONDITION_HANDLE}'

exit $INIT_STATUS