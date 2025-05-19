# Gateway

## Install Gateway by Helm

### DEV
```
helm install aim-inner-thehandsome-gw stable/gateway \
--namespace aim-gateway --create-namespace \
--values stable/gateway/values-private-dev.yaml

helm install aim-thehandsome-gw stable/gateway \
--namespace aim-gateway --create-namespace \
--values stable/gateway/values-public-dev.yaml
```

### STG
```
helm install aim-inner-thehandsome-gw stable/gateway \
--namespace aim-gateway --create-namespace \
--values stable/gateway/values-private-stg.yaml

helm install aim-thehandsome-gw stable/gateway \
--namespace aim-gateway --create-namespace \
--values stable/gateway/values-public-stg.yaml
```
### QA
```
helm install aim-inner-thehandsome-gw stable/gateway \
--namespace aim-gateway --create-namespace \
--values stable/gateway/values-private-qa.yaml

helm install aim-thehandsome-gw stable/gateway \
--namespace aim-gateway --create-namespace \
--values stable/gateway/values-public-qa.yaml
```

### PRD
```
helm install aim-inner-thehandsome-gw stable/gateway \
--namespace aim-gateway --create-namespace \
--values stable/gateway/values-private-prd.yaml

helm install aim-thehandsome-gw stable/gateway \
--namespace aim-gateway --create-namespace \
--values stable/gateway/values-public-prd.yaml
```


## Update Gateway by Helm

### DEV
```
helm upgrade aim-inner-thehandsome-gw stable/gateway \
--namespace aim-gateway --create-namespace \
--values stable/gateway/values-private-dev.yaml

helm upgrade aim-thehandsome-gw stable/gateway \
--namespace aim-gateway --create-namespace \
--values stable/gateway/values-public-dev.yaml
```

### STG
```
helm upgrade aim-inner-thehandsome-gw stable/gateway \
--namespace aim-gateway --create-namespace \
--values stable/gateway/values-private-stg.yaml

helm upgrade aim-thehandsome-gw stable/gateway \
--namespace aim-gateway --create-namespace \
--values stable/gateway/values-public-stg.yaml
```

### PRD
```
helm upgrade aim-inner-thehandsome-gw stable/gateway \
--namespace aim-gateway --create-namespace \
--values stable/gateway/values-private-prd.yaml

helm upgrade aim-thehandsome-gw stable/gateway \
--namespace aim-gateway --create-namespace \
--values stable/gateway/values-public-prd.yaml
```

## Uninstall Batch Scheduler by Helm

```
helm uninstall aim-inner-thehandsome-gw --namespace aim-gateway
helm uninstall aim-thehandsome-gw --namespace aim-gateway
```

