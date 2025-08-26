#!/bin/bash

# Script per testare la Lambda S3 Signature con diversi parametri
# Uso: ./test-s3-signature.sh [STACK_NAME] [REGION]

# Colori per output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configurazione
STACK_NAME=${1:-"java-aws-lambda-example04-signature"}
REGION=${2:-"eu-central-1"}
TEST_FILES=("test.pdf" "documents/report.docx" "images/photo.jpg" "data/export.csv")
EXPIRES_VALUES=(1 3 6 24)

echo -e "${BLUE}🔐 Test Lambda S3 Signature Function${NC}"
echo -e "${BLUE}====================================${NC}"
echo ""

# Recupera l'URL dell'API dallo stack CloudFormation
echo -e "${YELLOW}🔍 Recupero URL API dallo stack CloudFormation...${NC}"
API_URL=$(aws cloudformation describe-stacks \
  --stack-name "$STACK_NAME" \
  --region "$REGION" \
  --query 'Stacks[0].Outputs[?OutputKey==`S3SignatureApiUrl`].OutputValue' \
  --output text 2>/dev/null)

if [ -z "$API_URL" ] || [ "$API_URL" == "None" ]; then
    echo -e "${RED}❌ Impossibile recuperare l'URL dell'API dallo stack '$STACK_NAME' nella regione '$REGION'${NC}"
    echo -e "${YELLOW}💡 Verifica che lo stack esista e sia deployato correttamente${NC}"
    echo ""
    echo "Comandi utili per il debug:"
    echo "  aws cloudformation describe-stacks --stack-name $STACK_NAME --region $REGION"
    echo "  aws cloudformation list-stacks --region $REGION"
    exit 1
fi

echo -e "${GREEN}✅ API URL recuperato: $API_URL${NC}"
echo ""

# Test 1: Parametri validi
echo -e "${YELLOW}📋 Test 1: Parametri validi${NC}"
for file in "${TEST_FILES[@]}"; do
    for expires in "${EXPIRES_VALUES[@]}"; do
        echo -e "${BLUE}Testing:${NC} file='$file' expires='${expires}h'"
        
        response=$(curl -s "${API_URL}?key=${file}&expires=${expires}")
        status=$?
        
        if [ $status -eq 0 ]; then
            echo -e "${GREEN}✅ Success:${NC}"
            echo "$response" | jq '.' 2>/dev/null || echo "$response"
        else
            echo -e "${RED}❌ Failed: curl error${NC}"
        fi
        echo ""
    done
done

# Test 2: Parametri mancanti
echo -e "${YELLOW}📋 Test 2: Parametri mancanti${NC}"
echo -e "${BLUE}Testing:${NC} nessun parametro"
response=$(curl -s "${API_URL}")
echo -e "${RED}Expected error:${NC}"
echo "$response" | jq '.' 2>/dev/null || echo "$response"
echo ""

# Test 3: Parametri invalidi
echo -e "${YELLOW}📋 Test 3: Parametri invalidi${NC}"
echo -e "${BLUE}Testing:${NC} expires='invalid'"
response=$(curl -s "${API_URL}?key=test.pdf&expires=invalid")
echo -e "${BLUE}Response:${NC}"
echo "$response" | jq '.' 2>/dev/null || echo "$response"
echo ""

echo -e "${BLUE}Testing:${NC} expires='50' (over limit)"
response=$(curl -s "${API_URL}?key=test.pdf&expires=50")
echo -e "${BLUE}Response:${NC}"
echo "$response" | jq '.' 2>/dev/null || echo "$response"
echo ""

# Test 4: Test download (se jq è disponibile)
if command -v jq &> /dev/null; then
    echo -e "${YELLOW}📋 Test 4: Test download URL${NC}"
    echo -e "${BLUE}Testing:${NC} download with generated URL"
    
    presigned_url=$(curl -s "${API_URL}?key=test.pdf&expires=1" | jq -r '.presignedUrl')
    
    if [ "$presigned_url" != "null" ] && [ "$presigned_url" != "" ]; then
        echo -e "${GREEN}✅ Generated URL:${NC} ${presigned_url:0:80}..."
        
        # Test HEAD request per verificare accessibilità
        http_status=$(curl -s -o /dev/null -w "%{http_code}" -I "$presigned_url")
        
        if [ "$http_status" -eq 200 ]; then
            echo -e "${GREEN}✅ File accessible (HTTP 200)${NC}"
        elif [ "$http_status" -eq 404 ]; then
            echo -e "${YELLOW}⚠️  File not found (HTTP 404) - URL valid but file doesn't exist${NC}"
        else
            echo -e "${RED}❌ Unexpected HTTP status: $http_status${NC}"
        fi
    else
        echo -e "${RED}❌ Failed to extract presigned URL${NC}"
    fi
fi

echo ""
echo -e "${GREEN}🎉 Test completed!${NC}"
