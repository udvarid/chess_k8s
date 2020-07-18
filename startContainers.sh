echo start containers
sudo docker run -d --rm -p 8080:8080 --name my-chess-backend udvaridonat/donat-chess:backend
sudo docker run -d --rm -p 4200:4200 --name my-chess-frontend udvaridonat/donat-chess:frontend

echo clean up environment
sudo docker system prune -af