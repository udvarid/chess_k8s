#!/bin/bash
echo clean up docker images
sudo docker system prune -af

echo Backend application is building
cd backend/
sudo mvn clean install -DskipTests

echo building docker image
sudo docker build . -t udvaridonat/donat-chess:backend

echo pushing up the image to my repository
sudo docker push udvaridonat/donat-chess:backend

cd ..

cd frontend/don-chess/

echo refresh angular dependencies
sudo npm i

echo building project
sudo ng build

echo build the image
sudo docker build . -t udvaridonat/donat-chess:frontend

echo push image to my repository
sudo docker push udvaridonat/donat-chess:frontend

echo clean up docker images
sudo docker system prune -af

cd ../..


