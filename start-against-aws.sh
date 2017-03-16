sm --start DATASTREAM -f
sm --start SNS_CLIENT  --appendArgs '{
"SNS_CLIENT": [
"-Daws.region=eu-west-1",
"-Daws.accessKey=",
"-Daws.secret=",
"-Daws.platform.gcm.applicationArn=",
"-Daws.platform.gcm.osName=",
"-Daws.platform.gcm.apiKey=",
"-Daws.stubbing=false"]}' -f
