# Read Me First

# Getting Started

1. Install JAVA 17 
2. Install ngrok [Required for reverse proxy in Local Setup]
3. Go to resources/application.properties and fill the necessary properties
4. Open the browser and visit {HOST}/authorization/initiate/{userId}
   - E.g. http://localhost:8080/authorization/initiate/name.something@some-mail.com
5. Close the browser once you redirect to the #{${app.google-services.client.auth.code-redirection-host}${app.google-services.client.auth.code-redirection-uri}} as setup in the application.properties
6. Connect a folder using the following Curl -
<pre>
   curl --location --request POST '{HOST}/connect/user/root' \
   --header 'Content-Type: application/json' \
   --data-raw '{
   "userId": {userId  (Provided at step 4)},
   "rootFolder": {Folder Name To Connect To}
   }'
</pre>
7. Once you get successful response. You can check the 
