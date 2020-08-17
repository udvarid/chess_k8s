echo create mounted volume
sudo minikube ssh
sudo mkdir /mnt/data
sudo exit

echo delete previous version
helm delete chess --namespace chess
kubectl delete namespace chess

echo start new version
kubectl create namespace chess
helm install chess . --namespace chess
minikube addons enable ingress
