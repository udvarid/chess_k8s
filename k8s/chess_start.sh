echo delete previous deployments
kubectl delete -n chess deployment frontend
kubectl delete -n chess deployment backend
kubectl delete -n chess deployment postgre
kubectl delete -n chess service frontend
kubectl delete -n chess service backend
kubectl delete -n chess service postgre
kubectl delete -n chess ingress chess-ingress
kubectl delete -n chess ingress chess-ingress-backend
kubectl delete -n chess configmap chess-config
kubectl delete -n chess secret chess-secret

echo delete namespace
kubectl delete namespace chess

echo create namespace
kubectl create namespace chess

echo create config and secret
kubectl create -f config.yaml -n chess
kubectl create -f secret.yaml -n chess

echo create new deployments
kubectl create -f chess_db.yaml -n chess
kubectl create -f chess_db_s.yaml -n chess

kubectl create -f chess_backend.yaml -n chess
kubectl create -f chess_backend_s.yaml -n chess

kubectl create -f chess_frontend.yaml -n chess
kubectl create -f chess_frontend_s.yaml -n chess

echo turn on ingress
kubectl create -f chess_ingress.yaml -n chess
kubectl create -f chess_ingress_be.yaml -n chess
minikube addons enable ingress