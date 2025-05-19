# Install

## Fluentd 
### dev
```bash
helm install fluentd stable/fluentd -n aim-mgmt --set profiles.active=dev
```

### stg
```bash
helm install fluentd stable/fluentd -n aim-mgmt --set profiles.active=stg
```

### qa
```bash
helm install fluentd stable/fluentd -n aim-mgmt --set profiles.active=qa
```

### prd
```bash
helm install fluentd stable/fluentd -n aim-mgmt --set replicas=5
```

# Delete

## Fluentd
```bash
helm delete fluentd -n aim-mgmt
```
