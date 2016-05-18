sleep 3
echo Updating bot...
rm FredBoat-1.0.jar
mv update/target/FredBoat-1.0.jar FredBoat-1.0.jar
rm -rf update
java -jar FredBoat-1.0.jar