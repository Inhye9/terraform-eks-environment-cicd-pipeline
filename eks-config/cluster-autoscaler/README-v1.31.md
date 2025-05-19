# cluster-autoscaler(from v1.31) changed: kubectl apply -> helm  

# helm upgrade
helm upgrade cluster-autoscaler autoscaler/cluster-autoscaler -f /apps/workspace/aim-chart/stable/cluster-autoscaler/cluster-autoscaler-helm-v.31-qa-values.yaml 
