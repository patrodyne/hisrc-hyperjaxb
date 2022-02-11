#!/bin/sh
#
# Add the OTA namespace to schemas missing a target namepace.
#
TGTPATH="../target/doc/_2008A_XML"

cd ${TGTPATH}
for XSD in \
"OTA_AirCommonTypes.xsd" \
"OTA_AirPreferences.xsd" \
"OTA_CommonPrefs.xsd" \
"OTA_CommonTypes.xsd" \
"OTA_CruiseCommonTypes.xsd" \
"OTA_DestinationActivity.xsd" \
"OTA_GolfCommonTypes.xsd" \
"OTA_HotelCommonTypes.xsd" \
"OTA_HotelContentDescription.xsd" \
"OTA_HotelEvent.xsd" \
"OTA_HotelPreferences.xsd" \
"OTA_HotelRatePlan.xsd" \
"OTA_HotelReservation.xsd" \
"OTA_HotelRFP.xsd" \
"OTA_InsuranceCommonTypes.xsd" \
"OTA_LoyaltyCommonTypes.xsd" \
"OTA_MeetingProfile.xsd" \
"OTA_PkgCommonTypes.xsd" \
"OTA_PkgReservation.xsd" \
"OTA_Profile.xsd" \
"OTA_RailCommonTypes.xsd" \
"OTA_SimpleTypes.xsd" \
"OTA_TourInformation.xsd" \
"OTA_VehicleCommonTypes.xsd"
	do
		sed -E 's#(id="OTA200[3-8][AB]2008A")>#\1 xmlns="http://www.opentravel.org/OTA/2003/05" targetNamespace="http://www.opentravel.org/OTA/2003/05">#' "${XSD}" > "${XSD}.tmp" && mv "${XSD}.tmp" "${XSD}"
	done

# OTA_AirCommonTypes.xsd:	<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" version="7.000" id="OTA2003A2008A">
# OTA_AirPreferences.xsd:	<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" version="1.008" id="OTA2003A2008A">
# OTA_CommonPrefs.xsd:	<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" version="1.009" id="OTA2003A2008A">
# OTA_CommonTypes.xsd:	<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" version="2.003" id="OTA2003A2008A">
# OTA_CruiseCommonTypes.xsd:	<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" version="2.004" id="OTA2005B2008A">
# OTA_DestinationActivity.xsd:	<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" version="2.003" id="OTA2003A2008A">
# OTA_GolfCommonTypes.xsd:	<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" version="1.009" id="OTA2003A2008A">
# OTA_HotelCommonTypes.xsd:	<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" version="7.000" id="OTA2003A2008A">
# OTA_HotelContentDescription.xsd:	<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" version="5.003" id="OTA2003A2008A">
# OTA_HotelEvent.xsd:	<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" version="1.003" id="OTA2006B2008A">
# OTA_HotelPreferences.xsd:	<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" version="2.002" id="OTA2003A2008A">
# OTA_HotelRatePlan.xsd:	<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" version="1.001" id="OTA2007B2008A">
# OTA_HotelReservation.xsd:	<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" version="4.000" id="OTA2003A2008A">
# OTA_HotelRFP.xsd:	<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" version="5.000" id="OTA2003A2008A">
# OTA_InsuranceCommonTypes.xsd:	<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" version="1.010" id="OTA2003A2008A">
# OTA_LoyaltyCommonTypes.xsd:	<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" version="1.010" id="OTA2003A2008A">
# OTA_MeetingProfile.xsd:	<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" version="1.010" id="OTA2003A2008A">
# OTA_PkgCommonTypes.xsd:	<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" version="4.003" id="OTA2003A2008A">
# OTA_PkgReservation.xsd:	<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" version="3.006" id="OTA2006A2008A">
# OTA_Profile.xsd:	<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" version="3.000" id="OTA2003A2008A">
# OTA_RailCommonTypes.xsd:	<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" version="1.010" id="OTA2003A2008A">
# OTA_SimpleTypes.xsd:	<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" version="2.006" id="OTA2003A2008A">
# OTA_TourInformation.xsd:	<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" version="1.000" id="OTA2008A2008A">
# OTA_VehicleCommonTypes.xsd:	<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" version="4.003" id="OTA2003A2008A">
