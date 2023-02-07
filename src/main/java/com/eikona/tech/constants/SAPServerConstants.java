package com.eikona.tech.constants;

public class SAPServerConstants {
	
	public static final String  D ="d";
	public static final String  RESULTS="results";
	public static final String  USER_NAV="userNav";
	public static final String  START_DATE="startDate";
	public static final String  DEFAULT_FULL_NAME="defaultFullName";
	public static final String  USER_ID="userId";
	
	public static final String  USER_API_GET_ALL="https://api44.sapsf.com//odata/v2/EmpJob?"+
									    "$select=departmentNav/name,emplStatusNav/picklistLabels/label,employmentNav/personNav/personalInfoNav/"+
									    "firstName,employmentNav/personNav/personalInfoNav/lastName,endDate,event,eventNav/picklistLabels/"+
									    "label,eventReason,eventReasonNav/name,seqNumber,startDate,userId&$expand=departmentNav,emplStatusNav/"+
									    "picklistLabels,employmentNav/personNav/personalInfoNav,eventNav/picklistLabels,eventReasonNav&$filter=emplStatusNav/"+
									    "externalCode eq 'A'&$format=json&$top=%s&$skip=%s";
	
	public static final String  USER_TIME_INFO_API_BY_SHIFT="https://api44.sapsf.com/odata/v2/TemporaryTimeInformation?$select=endDate,externalCode,startDate,userId,"+
										"workSchedule,workScheduleNav/externalName_defaultValue,workScheduleNav/workScheduleDayModels/segments/endTime,workScheduleNav/"+
										"workScheduleDayModels/segments/startTime&$expand=workScheduleNav/workScheduleDayModels/segments&$format=json"+
										"&$filter=workSchedule eq '%s' and startDate eq '%sT00:00:00Z'&$top=%s&$skip=%s";
	
}
