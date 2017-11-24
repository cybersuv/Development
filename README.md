# Development
Development Repository


The REST Call specification

# URL : 
http(s)://{Application Root}/upload
# Headers :
Az-Account = {storage account name}

Az-Key = {storage account access key}

Az-Signature = {SAS key [Not used now, put any value]}

Az-Share = {Target share container name}

# Body :
Content-type : form-data

Parameter name = file (Type file)
