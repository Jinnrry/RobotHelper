GOOS=linux GOARCH=arm64 go build -o ./bin/arm64/sendevent
GOOS=linux GOARCH=arm go build -o ./bin/arm/sendevent
GOOS=linux GOARCH=386 go build -o ./bin/x86/sendevent
GOOS=linux GOARCH=amd64 go build -o ./bin/x64/sendevent