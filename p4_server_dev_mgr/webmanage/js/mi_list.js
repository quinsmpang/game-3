function refreshIt(nType)
{
	if(nType != null) RefreshForm.ListType.value = nType;
	RefreshForm.submit();
}

function viewIt(nCoverId, strFullPNo)
{
	ViewMIForm.mi_cover_id.value = nCoverId;
	ViewMIForm.pno.value = strFullPNo;
	ViewMIForm.submit();
}

function setAllStatus(nCoverId, nStatus, strTitle, strConfirm)
{
	if(strConfirm != null)
	{
		if(confirm(strConfirm)==false)	return;
	}

	rv = ModalDialog("/smemics/pe/mi_control_set.jsp?mi_cover_id="+nCoverId+"&newstatus="+nStatus,strTitle,500,360,'yes');
	if(rv != null && rv == -1) top.close();
	RefreshForm.submit();
}
