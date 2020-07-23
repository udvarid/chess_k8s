echo delete previous deployments
kubectl delete -n chess deployment frontend
kubectl delete -n chess deployment backend
kubectl delete -n chess service frontend
kubectl delete -n chess service backend
kubectl delete -n chess ingress chess-ingress

echo delete namespace
kubectl delete namespace chess

echo create namespace
kubectl create namespace chess

echo create new deployments
kubectl create -f chess_backend.yaml -n chess
kubectl create -f chess_backend_s.yaml -n chess
kubectl create -f chess_frontend.yaml -n chess
kubectl create -f chess_frontend_s.yaml -n chess
kubectl create -f chess_ingress.yaml -n chess
