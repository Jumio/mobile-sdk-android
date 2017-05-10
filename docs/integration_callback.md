![Jumio](images/jumio_feature_graphic.png)

# Callback

## Table of Content

- [Callback for Netverify](#callback-for-netverify)
- [Callback for Netverify Multi Document](#callback-for-netverify-multi-document)
- [Netverify Retrieval API](#netverify-retrieval-api)
- [Netverify Delete API](#netverify-delete-api)
- [Global Netverify settings](#globa-lnetverify-settings)
- [Supported cipher suites](#supported-cipher-suites)
- [Two-factor authentication](#two-factor-authentication)

## Callback for Netverify

If ID verification is enabled, an HTTP POST is sent to your specified callback URL containing an `application/x-www-form-urlencoded` formatted string with the result.

|User journey state       | Scan state    | Callback|
|:---------------|:--------|:------------|
|Drop off   | Pending => Failed  |Transaction will be cleaned-up from pending to failed 15 minutes after last update<br/>Callback: verification status NO_ID_UPLOADED|
|Finished       | Done  |ID verification will be performed<br/>Callback: verification status depends on the result (see table below)|

The following parameters are posted to your callback URL.

__Note:__ Mandatory parameters are highlighted bold.

|Parameter       | Max. length    | Description|
|:---------------|:--------|:------------|
|__callBackType__   |   |NETVERIFYID |
|__jumioIdScanReference__   |36  |Jumio’s reference number for each scan |
|__verificationStatus__   |   |Possible states:<br/>•	APPROVED_VERIFIED<br/> •	DENIED_FRAUD<br/>•	DENIED_UNSUPPORTED_ID_TYPE (`*1`)<br/> •	DENIED_UNSUPPORTED_ID_COUNTRY (`*1`)<br/>•	ERROR_NOT_READABLE_ID<br/> •	NO_ID_UPLOADED |
|__idScanStatus__   |   |SUCCESS if verificationStatus = APPROVED_VERIFIED, otherwise ERROR |
|__idScanSource__   |   |SDK |
|__idCheckDataPositions__   |   |"OK" if verificationStatus = APPROVED_VERIFIED, otherwise "N/A" |
|__idCheckDocumentValidation__   |   |"OK" if verificationStatus = APPROVED_VERIFIED, otherwise "N/A" |
|__idCheckHologram__   |   |"OK" if verificationStatus = APPROVED_VERIFIED, otherwise "N/A" |
|__idCheckMRZcode__   |   |"OK" for passports and supported ID cards if verificationStatus = APPROVED_VERIFIED and MRZ check is enabled, otherwise "N/A" |
|__idCheckMicroprint__   |   |"OK" if verificationStatus = APPROVED_VERIFIED, otherwise "N/A" |
|__idCheckSecurityFeatures__   |   |"OK" if verificationStatus = APPROVED_VERIFIED, otherwise "N/A" |
|__idCheckSignature__   |   |"OK" if verificationStatus = APPROVED_VERIFIED, otherwise "N/A" |
|__transactionDate__   |   |Timestamp of the scan creation in the format YYYY-MM-DDThh:mm:ss.SSSZ |
|__callbackDate__   |   |Timestamp of the callback creation in the format YYYY-MM-DDThh:mm:ss.SSSZ |
|idType   |   |Possible types:<br/>•	PASSPORT<br/>•	DRIVING_LICENSE<br/>•	ID_CARD |
|idSubtype   |255  |Possible subtypes if idType = ID_CARD<br/>•	NATIONAL_ID<br/>•	CONSULAR_ID<br/>•	ELECTORAL_ID<br/>•	RESIDENT_PERMIT_ID<br/>•	TAX_ID (only supported for PHL)<br/>•	STUDENT_ID (only supported for POL)<br/>•	PASSPORT_CARD_ID (only supported for IRL, RUS and USA)<br/>•	MILITARY_ID (only supported for GRC)<br/>•	OTHER_ID<br/>•	VISA (only supported for USA)<br/>•	UNKNOWN<br/><br/>Possible subtypes if idType = DRIVING_LICENSE<br/>•	LEARNING_DRIVING_LICENSE (only supported for GBR, IRL, BEL and CAN)<br/><br/>Possible subtypes if idType = PASSPORT<br/>•	E_PASSPORT (only for mobile) |
|idCountry   |3  |Possible countries:<br/>•	[ISO 3166-1 alpha-3](http://en.wikipedia.org/wiki/ISO_3166-1_alpha-3) country code<br/>•	XKX (Kosovo)|
|rejectReason   |   |Reject reason as JSON object if verificationStatus = DENIED_FRAUD or ERROR_NOT_READABLE_ID, see tables below  |
|idFaceMatch   |   |Face match percentage 0-100 if verificationStatus = APPROVED_VERIFIED (`*2`) |
|idScanImage   |255  |URL to the image of the scan (JPEG or PNG) if available (`*3`) |
|idScanImageFace   |255  |URL to the face image of the scan (JPEG or PNG) if available (`*3`)|
|idScanImageBackside   |255  |URL to the back side image of the scan (JPEG or PNG) if available (`*3`)|
|idNumber   |200  |Identification number of the document as available on the ID if verificationStatus = APPROVED_VERIFIED and enabled |
|idFirstName   |200  |•	First name of the customer as available on the ID if verificationStatus = APPROVED_VERIFIED and enabled, otherwise if provided<br/>•	N/A (for non-Latin characters)<br /> -  if idCountry = CHN and idType = DRIVING_LICENSE or ID_CARD<br /> - if idCountry = JPN and idType = DRIVING_LICENSE<br /> - if idCountry = KOR and idType = DRIVING_LICENSE or ID_CARD |
|idLastName   |200  |•	Last name of the customer as available on the ID if verificationStatus = APPROVED_VERIFIED and enabled, otherwise if provided<br/>•	Only if full name is printed in Latin characters<br />- if idCountry = KOR and idType = DRIVING_LICENSE (first name and last name)<br /> •	N/A (for non-Latin characters)<br /> - if idCountry = CHN and idType = DRIVING_LICENSE or ID_CARD<br /> -	if idCountry = JPN and idType = DRIVING_LICENSE<br /> - if idCountry = KOR and idType = DRIVING_LICENSE or ID_CARD|
|idDob   |10  |Date of birth in the format YYYY-MM-DD as available on the ID if verificationStatus = APPROVED_VERIFIED and enabled |
|idExpiry   |10  |Date of expiry in the format YYYY-MM-DD as available on the ID if verificationStatus = APPROVED_VERIFIED and enabled |
|idUsState   |100  |Possible values if idType = PASSPORT or ID_CARD:<br/>•	Last two characters of [ISO 3166-2:US](http://en.wikipedia.org/wiki/ISO_3166-2:US) state code<br/>•	[ISO 3166-1](http://en.wikipedia.org/wiki/ISO_3166-1) country name<br/>•	Kosovo<br/><br/>If idType = DRIVING_LICENSE:<br/>•	Last two characters of [ISO 3166-2:US](http://en.wikipedia.org/wiki/ISO_3166-2:US) state code|
|personalNumber   |14  |Personal number of the document as available on the ID if verificationStatus = APPROVED_VERIFIED and available |
|idAddress   |   |Address as JSON object in US, EU or raw format if verificationStatus = APPROVED, see tables below (`*4`) |
|merchantIdScanReference   |100  |Your reference for each scan |
|merchantReportingCriteria   |100  |Your reporting criteria for each scan |
|customerId   |100  |ID of the customer as provided |
|additionalInformation   |100  |Additional information as provided |
|clientIp   |   |IP address of the client in the format xxx.xxx.xxx.xxx |
|firstAttemptDate   |  |Timestamp of the first scan attempt in the format YYYY-MM-DDThh:mm:ss.SSSZ |
|optionalData1   |255  |Optional field of MRZ line 1 |
|optionalData2   |255  |Optional field of MRZ line 2 |
|dni   |255  |DNI as available on the ID if idCountry = ESP and idSubtype = NATIONAL_ID  |
|gender   |2  |Possible values if idCountry = FRA and idSubtype = NATIONAL_ID (MRZ type CNIS)<br/>•	M<br/>•	F |
|idFaceLiveness   |   |only available for SDK<br/>Possible values:<br/>•	TRUE (if face match enabled for ID verification and liveness detected successfully during scanning)<br/>•	FALSE |

(`*1`) Scan is declined as unsupported if the provided ID is not supported by Jumio or not accepted in your Netverify settings.<br/>
(`*2`) Face match is performed if enabled.<br/>
(`*3`) For ID types that are configured to support a separate scan of front side and back side, there is a separated image of the front side (idScanImage) and the back side (idScanImageBackside). If face match is enabled, there is also picture of the face (idScanImageFace).
To access the image, use the HTTP GET method and HTTP Basic Authentication with your merchant API token as the "userid" and your API secret as the "password". Set "User-Agent: YOURCOMPANYNAME YOURAPPLICATIONNAME/VERSION" (e.g. MyCompany MyApp/1.0.0) in the "header" section of your request. The TLS protocol is required during the TLS handshake (see Supported cipher suitesSupported cipher suites chapter) and we strongly recommend using the latest version.<br/>
(`*4`) Address recognition is performed for supported IDs if enabled. There are three different address formats. You can see which format applies to specific IDs under "Data settings" in your Jumio merchant settings. Different address parameters are part of the JSON object if they are available on the ID.

### US address format

|Parameter "idAddress"       | Max. length    | Description|
|:---------------|:--------|:------------|
|city   |64  |City |
|stateCode   |6  |[ISO 3166-2](http://en.wikipedia.org/wiki/ISO_3166-2) state code |
|streetName   |64  |Street name |
|streetSuffix   |14  |Street suffix abbreviation<br/>Examples: [US](http://www.gis.co.clay.mn.us/USPS.htm#suffix), [Canada](http://www.canadapost.ca/tools/pg/manual/PGaddress-e.asp#1423617), [Australia](https://auspost.com.au/media/documents/australia-post-addressing-standards-1999.pdf) |
|streetDirection   |4  |Street direction abbreviation<br/>Examples: US (E=EAST, W=WEST, N=NORTH, S=SOUTH), [Canada](http://www.canadapost.ca/tools/pg/manual/PGaddress-e.asp#1403220), [Australia](https://auspost.com.au/media/documents/australia-post-addressing-standards-1999.pdf) |
|streetNumber   |14  |Street number |
|unitDesignator   |14  |Unit designator abbreviation<br/>Examples: [US](http://www.gis.co.clay.mn.us/USPS.htm#secunitdesig), [Canada](http://www.canadapost.ca/tools/pg/manual/PGaddress-e.asp#1380473), [Australia](https://auspost.com.au/media/documents/australia-post-addressing-standards-1999.pdf) |
|unitNumber   |14  |Unit number |
|zip   |14  |Zip code |
|zipExtension   |20  |Zip extension |
|country   |3  |Possible countries:<br/>•	[ISO 3166-1 alpha-3](http://en.wikipedia.org/wiki/ISO_3166-1_alpha-3) country code<br/>•	XKX (Kosovo) |

### EU address format

|Parameter "idAddress"       | Max. length    | Description|
|:---------------|:--------|:------------|
|city   |64  |City |
|province   |64  |Province |
|streetName   |64  |Street name |
|streetNumber   |15  |Street number |
|unitDetails   |64  |Unit details |
|postalCode   |15  |Postal code |
|country   |3  |Possible countries:<br/>•	[ISO 3166-1 alpha-3](http://en.wikipedia.org/wiki/ISO_3166-1_alpha-3) country code<br/>•	XKX (Kosovo) |

### Raw address format

|Parameter "idAddress"       | Max. length    | Description|
|:---------------|:--------|:------------|
|line1   |100  |Line item 1 |
|line2   |100  |Line item 2 |
|line3   |100  |Line item 3 |
|line4   |100  |Line item 4 |
|line5   |100  |Line item 5 |
|country   |3  |Possible countries:<br/>•	[ISO 3166-1 alpha-3](http://en.wikipedia.org/wiki/ISO_3166-1_alpha-3) country code<br/>•	XKX (Kosovo)  |
|postalCode   |15  |Postal code |
|city   |64  |City |

### Reject reason

|Parameter "rejectReason"       | Max. length    | Description|
|:---------------|:--------|:------------|
|__rejectReasonCode__   |5  |see below |
|__rejectReasonDescription__   |64  |100	MANIPULATED_DOCUMENT<br/>105	FRAUDSTER<br/>106	FAKE<br/>107	PHOTO_MISMATCH<br/>108	MRZ_CHECK_FAILED<br/>109	PUNCHED_DOCUMENT<br/>110	CHIP_DATA_MANIPULATED (only available for ePassport)<br/>111	MISMATCH_PRINTED_BARCODE_DATA<br/><br/>Possible codes and descriptions if verificationStatus = ERROR_NOT_READABLE_ID:<br/>102	PHOTOCOPY_BLACK_WHITE<br/>103	PHOTOCOPY_COLOR<br/>104	DIGITAL_COPY<br/>200	NOT_READABLE_DOCUMENT<br/>201	NO_DOCUMENT<br/>202	SAMPLE_DOCUMENT<br/>206	MISSING_BACK<br/>207	WRONG_DOCUMENT_PAGE<br/>209	MISSING_SIGNATURE<br/>210	CAMERA_BLACK_WHITE<br/>211	DIFFERENT_PERSONS_SHOWN<br/>212	IMAGE_NOT_MATCHING_DATA (only available for ePassport)<br/>300	MANUAL_REJECTION|
|rejectReasonDetails   |   |Reject reason details as JSON array containing JSON objects if rejectReasonCode = 100 or 200, see table below |
|rejectReasonDetails   |   |Reject reason details as JSON array containing JSON objects if rejectReasonCode = 100 or 200, see table below |

### Reject reason details

|Parameter "rejectReasonDetails"       | Max. length    | Description|
|:---------------|:--------|:------------|
|detailsCode   |5  |Possible codes and descriptions if rejectReasonCode = 100:<br/>1001	PHOTO<br/>1002	DOCUMENT_NUMBER<br/>1003	EXPIRY<br/>1004	DOB<br/>1005	NAME<br/>1006	ADDRESS<br/>1007	SECURITY_CHECKS<br/>1008	SIGNATURE<br/>1009	PERSONAL_NUMBER<br/>Possible codes and descriptions if<br/><br/>rejectReasonCode = 200:<br/>2001	BLURRED<br/>2002	BAD_QUALITY<br/>2003	MISSING_PART_DOCUMENT<br/>2004	HIDDEN_PART_DOCUMENT<br/>2005	DAMAGED_DOCUMENT |

### Sample callbacks

#### Sample callback (URL-encoded POST): Approved and verified

```
idExpiry=2022-12-31&idType=PASSPORT&idDob=1990-01-01&idCheckSignature=OK&idCheckDataPositions=OK&idCheckHologram=OK&idCheckMicroprint=OK&idCheckDocumentValidation=OK&idCountry=USA&idScanSource=SDK&idFirstName=FIRSTNAME&verificationStatus=APPROVED_VERIFIED&jumioIdScanReference=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx&personalNumber=N%2FA&merchantIdScanReference=YOURIDSCANREFERENCE&idCheckSecurityFeatures=OK&idCheckMRZcode=OK&idScanImage=https%3A%2F%2Fnetverify.com%2Frecognition%2Fv1%2Fidscan%2Fxxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx%2Ffront&callBackType=NETVERIFYID&clientIp=xxx.xxx.xxx.xxx&idLastName=LASTNAME&idAddress=%7B%22country%22%3A%22USA%22%2C%22stateCode%22%3A%22US%2DOH%22%7D&idScanStatus=SUCCESS&idNumber=P1234
```

#### Sample callback (URL-encoded POST): Fraud

```
idType=PASSPORT&idCheckSignature=N%2FA&rejectReason=%7B%20%22rejectReasonCode%22%3A%22100%22%2C%20%22rejectReasonDescription%22%3A%22MANIPULATED_DOCUMENT%22%2C%20%22rejectReasonDetails%22%3A%20%5B%7B%20%22detailsCode%22%3A%20%221001%22%2C%20%22detailsDescription%22%3A%20%22PHOTO%22%20%7D%2C%7B%20%22detailsCode%22%3A%20%221004%22%2C%20%22detailsDescription%22%3A%20%22DOB%22%20%7D%5D%7D&idCheckDataPositions=N%2FA&idCheckHologram=N%2FA&idCheckMicroprint=N%2FA&idCheckDocumentValidation=N%2FA&idCountry=USA&idScanSource=SDK&verificationStatus=DENIED_FRAUD&jumioIdScanReference=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx&merchantIdScanReference=YOURSCANREFERENCE&idCheckSecurityFeatures=N%2FA&idCheckMRZcode=N%2FA&idScanImage=https%3A%2F%2Fnetverify.com%2Frecognition%2Fv1%2Fidscan%2Fxxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx%2Ffront&callBackType=NETVERIFYID&clientIp=xxx.xxx.xxx.xxx&idScanStatus=ERROR
```

---

## Callback for Netverify Multi Document

### Parameters

An HTTP POST with the following parameters is sent to your specified callback URL containing an `application/x-www-form-urlencoded` formatted string with the result.

__Note:__ Mandatory parameters are highlighted bold.

|Parameter       | Type    | Max. length|  Description|
|:---------------|:--------|:----------: |:------------|
|__scanReference__   | String  |36          |Jumio's reference number for each scan|
|__timestamp__       | String  |   	        |Timestamp of the response in the format YYYY-MM-DDThh:mm:ss.SSSZ|
|__transaction__     | JSON object  |       |Transaction related data, see table below|
|document            | JSON object  |       |Document related data if transaction status = DONE, see table |

|Parameter "transaction"       | Type    | Max. length|  Description|
|:---------------|:--------|:----------:|:------------|
|__date__   					        | String  |    |Timestamp of the scan creation in the format YYYY-MM-DDThh:mm:ss.SSSZ|
|__status__     							| String  |    |DONE|
|__source__     							| String  |    |DOC_SDK|
|__merchantScanReference__		| String  |255 |Your reference for each scan |
|__customerId__       				| String  |255 |ID of the customer|
|additionalInformation        | String  |255 |Additional information|
|merchantReportingCriteria    | String  |255 |Your reporting criteria for each scan|
|clientIp       					    | String  |100  |IP address of the client if provided for the Netverify Multi Document API |

|Parameter "document"      | Type    | Max. length|  Description|
|:-------------------------|:--------|:----------:|:------------|
|__status__   					| String  |    |Possible states: <br/> ⦁ UPLOADED (default) <br/> ⦁	EXTRACTED if type = SSC, country = USA and US social security card provided <br/> ⦁	DISCARDED if type = SSC, country = USA and no US social security card provided |
|__country__     				| String  |3   |Possible countries: <br/> ⦁ [ISO 3166-1 alpha-3](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-3) country code <br/> ⦁	XKX (Kosovo) |
|__type__     					| String  |    |Possible types: <br/>⦁	BS (Bank statement, front side) <br/>⦁	IC (Insurance card, front side) <br/>⦁	UB (Utility bill, front side) <br/>⦁	CAAP (Cash advance application, front and back side) <br/>⦁	CRC (Corporate resolution certificate, front and back side) <br/>⦁	CCS (Credit card statement, front and back side) <br/>⦁	LAG (Lease agreement, front and back side) <br/>⦁	LOAP (Loan application, front and back side) <br/>⦁	MOAP (Mortgage application, front and back side) <br/>⦁	TR (Tax return, front and back side) <br/>⦁	VT (Vehicle title, front side) <br/>⦁	VC (Voided check, front side) <br/>⦁	STUC (Student card, front side) <br/>⦁	HCC (Health care card, front side) <br/>⦁	CB (Council bill, front side) <br/>⦁	SENC (Seniors card, front side) <br/>⦁	MEDC (Medicare card, front side) <br/>⦁	BC (Birth certificate, front side) <br/>⦁	WWCC (Working with children check, front side) <br/>⦁	SS (Superannuation statement, front side) <br/>⦁	TAC (Trade association card, front side) <br/>⦁	SEL (School enrolment letter, front side) <br/>⦁	PB (Phone bill, front side) <br/>⦁	USSS (US social security card, front side) <br/>⦁	SSC (Social security card, front side) <br/>⦁	CUSTOM (Custom document type)|
|__images__							| JSON array  |  |URLs to the images of the scan (JPEG or PNG) To access an image, use the HTTP GET method and HTTP Basic Authentication with your merchant API token as the "userid" and your API secret as the "password". Set "User-Agent: YOURCOMPANYNAME YOURAPPLICATIONNAME/VERSION" (e.g. MyCompany MyApp/1.0.0) in the "header" section of your request. The TLS protocol is required during the TLS handshake (see Supported cipher suites chapter) and we strongly recommend using the latest version |
|customDocumentCode | String  |100 |Your custom document code (maintained in your Jumio merchant backend) if type = CUSTOM |
|extractedData      | JSON object  | |Extracted data if status = EXTRACTED, see Supported documents for data extraction|

|Parameter "extractedData"      | Type    | Max. length|  Description|
|:---------------|:--------|:----------:|:------------|
|firstName  | String  |255    |First name if readable|
|lastName|String|255|Last name if readable|
|name|String| 100| Full name if readable|
|ssn     							| String  |255 |Social security number if readable|
|signatureAvailable  | String  |   |"true" if signature available, otherwise "false"|
|accountNumber|String|28|Bank account number of the customer from a bank statement|
|firstName     				| String  |255 |First name if readable|
|lastName							| String  |255 |Last name if readable |

### Sample callbacks

#### Sample callback (URL-encoded POST): UPLOADED
```
timestamp=2016-06-06T12%3A06%3A49.016Z&scanReference=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx&document=%7B%22type%22%3A%22SSC%22%2C%22country%22%3A%22AUT%22%2C%22images%22%3A%5B%22https%3A%2F%2Fretrieval.netverify.com%2Fapi%2Fnetverify%2Fv2%2Fdocuments%2Fxxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx%2Fpages%2F1%22%5D%2C%22status%22%3A%22UPLOADED%22%7D&transaction=%7B%22customerId%22%3A%22CUSTOMERID%22%2C%22date%22%3A%222014-10-17T06%3A37%3A51.969Z%22%2C%22merchantScanReference%22%3A%22YOURSCANREFERENCE%22%2C%22source%22%3A%22DOC_SDK%22%2C%22status%22%3A%22DONE%22%7D
```

#### Sample callback (URL-encoded POST): EXTRACTED
```
timestamp=2016-06-06T12%3A06%3A49.016Z&scanReference=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx&document=%7B%22type%22%3A%22SSC%22%2C%22country%22%3A%22USA%22%2C%22extractedData%22%3A%7B%22firstName%22%3A%22FIRSTNAME%22%2C%22lastName%22%3A%22LASTNAME%22%2C%22signatureAvailable%22%3Atrue%2C%22ssn%22%3A%22xxxxxxxxx%22%7D%2C%22images%22%3A%5B%22https%3A%2F%2Fretrieval.netverify.com%2Fapi%2Fnetverify%2Fv2%2Fdocuments%2Fxxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx%2Fpages%2F1%22%5D%2C%22status%22%3A%22EXTRACTED%22%7D&transaction=%7B%22customerId%22%3A%22CUSTOMERID%22%2C%22date%22%3A%222014-10-17T06%3A37%3A51.969Z%22%2C%22merchantScanReference%22%3A%22YOURSCANREFERENCE%22%2C%22source%22%3A%22DOC_SDK%22%2C%22status%22%3A%22DONE%22%7D
```

#### Sample callback (URL-encoded POST): DISCARDED
```
timestamp=2016-06-06T12%3A06%3A49.016Z&scanReference=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx&document=%7B%22type%22%3A%22SSC%22%2C%22country%22%3A%22USA%22%2C%22images%22%3A%5B%22https%3A%2F%2Fretrieval.netverify.com%2Fapi%2Fnetverify%2Fv2%2Fdocuments%2Fxxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx%2Fpages%2F1%22%5D%2C%22status%22%3A%22DISCARDED%22%7D&transaction=%7B%22customerId%22%3A%22CUSTOMERID%22%2C%22date%22%3A%222014-10-17T06%3A37%3A51.969Z%22%2C%22merchantScanReference%22%3A%22YOURSCANREFERENCE%22%2C%22source%22%3A%22DOC_SDK%22%2C%22status%22%3A%22DONE%22%7D
```
---
## Netverify Retrieval API
If your server was not able to process the callback, which is the authoritative answer from Jumio, you can implement RESTful HTTP GET APIs to retrieve status, details and image(s) for a specific scan. Find the Implementation Guide at the link below.

http://www.jumio.com/implementation-guides/netverify-retrieval-api/

---

## Netverify Delete API
You can implement the RESTful DELETE API to remove sensitive data (e.g. name, address, date of birth, document number, etc.) and image(s) of a finished scan. Find the Implementation Guide at the link below.

https://www.jumio.com/implementation-guides/netverify-delete-api/

---

## Global Netverify settings
In your Jumio merchant settings, you can configure the ID verification as follows.

### Application settings

#### Callback URL

Provide a URL which meets the following constraints:
* HTTPS using the TLS protocol (we strongly recommend using the latest version)
* Valid URL (RFC-2396) using ASCII characters or IDNA Punycode
* IPs, ports, query parameters and fragment identifiers are not allowed

Whitelist the following IP addresses for callbacks, and use these to verify that the callback originated from Jumio:

* US data center: 50.57.26.248, 50.57.26.249, 69.20.24.138, 162.242.200.222, 184.106.91.66, 184.106.91.67, 104.130.61.196, 146.20.77.156. </br>
You can look up the IP addresses with the host name "callback.jumio.com".
* EU data center: 162.13.228.132, 162.13.228.134, 162.13.229.103, 162.13.229.104. <br/>
You can look up the IP addresses with the host name "callback.lon.jumio.com".

Jumio will post callbacks to your HTTPS URL if you are using a valid certificate to ensure a successful TLS handshake. If you are not receiving callbacks, please check the following [article](https://support.jumio.com/hc/en-us/articles/200275338-I-am-not-receiving-callbacks-What-can-I-do).

### Accepted IDs
You can configure accepted IDs per region or country. The default setup includes all countries and ID types supported by Jumio at the time when your account was created.

__Note:__ You can disable the option to automatically accept newly supported IDs by Jumio.

### Data settings
You can choose which fields should be processed during the ID verification.

#### Mandatory fields
Mandatory fields will be returned in the callback for all Jumio supported IDs, if enabled.
* Date of birth
* ID number
* First name
* Last name

#### Optional fields
Optional fields will be returned in the callback under certain conditions, if enabled.
* Expiry Date (if availble on the ID)
* US state (if US ID)
* Personal number (if available on the ID)
* MRZ check (if passport for MRZ check supported for the ID card)

__Note:__ To perform the MRZ check, the fields date of birth, expiry and personal number will be processed during the ID verification and returned in the callback, if available on the ID.

__Supported countries for ID card MRZ check:__ Albania, Argentina, Austria, Bosnia & Herzegovina, Bulgaria, Chile, Croatia, Czech Republic, Denmark, Dominican Republic, Ecuador, El Salvador, Estonia, Finland, France, Georgia, Germany, Guatemala, Hungary, Italy, Kazakhstan, Kenya, Latvia, Liechtenstein, Lithuania, Macedonia, Malta, Mexico , Moldova, Montenegro, Netherlands, Pakistan, Paraguay, Peru, Poland, Portugal, Romania, Serbia, Slovakia, Slovenia, Spain, Sweden, Switzerland, Turkey, United Arab Emirates

#### Data retention
Select a time interval for permanent purge of sensitive data (5 years by default). The deletion of fraud transactions can be enabled (excluded by default).

### Multi documents

#### Custom

In your Jumio merchant settings, you can create your own custom document types. After submitting a new custom document type it takes up to five minutes until it is applied.

Specify
* a unique document code (constraint: Blanks are not allowed),
* a name and
* a default label name in English.

It is possible to add translations for different languages.

__Note:__ All fields can be updated after creation, except the document code. Created document types cannot be deleted, but they can be disabled (because of the unique document code which cannot be reused).

---

## Supported cipher suites
The following cipher suites (listed in server-preferred order) are supported by Jumio during the TLS handshake:

* TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256
* TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256
* TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384
* TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384
* TLS_RSA_WITH_AES_256_GCM_SHA384
* TLS_RSA_WITH_AES_256_CBC_SHA256
* TLS_RSA_WITH_AES_128_GCM_SHA256
* TLS_RSA_WITH_AES_128_CBC_SHA256
* TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA
* TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA
* TLS_RSA_WITH_AES_128_CBC_SHA
* TLS_RSA_WITH_AES_256_CBC_SHA

---

## Two-factor authentication
If you want to enable two-factor authentication for your Jumio merchant backend please contact us at https://support.jumio.com. Once enabled, users will be guided through the setup upon their first login to obtain a security code using the *"Google Authenticator"* app.

## Copyright

&copy; Jumio Corp. 268 Lambert Avenue, Palo Alto, CA 94306
