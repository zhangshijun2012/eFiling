<#-- 此为档案借阅即将到期时提醒借阅人的邮件 -->
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<#include "/mail/css.ftl" />
</head>
<body>
<p class="recipient">${ recipient!to },您好:</p>
<p>您有${ data?size }份借出的档案即将到期,请尽快归还.</p>
<p>
	<table>
		<thead>
			<tr>
				<th>序号</th>
				<th>保/批单号</th>
				<th>档案编码</th>
				<th>资料类型</th>
				<th>借阅日期</th>
				<th>预计归还日期</th>
			</tr>
		</thead>
		<tbody class="nowrap">
		<#list data as row>
			<tr class="<#if (row_index + 1) % 2 == 1>odd<#else>even</#if>">
				<td class="number">${ row_index + 1 }</td>
				<td>${ row[0]!"" }</td>
				<td>${ row[1]!"" }</td>
				<td>${ row[2]!"" }</td>
				<td class="date">${ row[3]!"" }</td>
				<td class="date">${ row[4]!"" }</td>
			</tr>
		</#list>
		</tbody>
	</table>
</p>
</body>
</html>
