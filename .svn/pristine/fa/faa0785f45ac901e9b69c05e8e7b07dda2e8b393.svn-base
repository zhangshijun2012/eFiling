<#-- 此为在即将超期时提醒业务人员的邮件格式.ftl的根目录为ftl,include必须从个目录开始写路径 -->
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>投保资料差缺到期提醒</title>
	<#include "/mail/css.ftl" />
</head>
<body>
<p class="recipient">${ recipient!to },您好:</p>
<#if data??>
<p>您有${ data?size }份差缺的资料即将到期,请尽快提交资料,若超期后尚未补回,将无法提交投保单.</p>
<p>
	<table>
		<thead>
			<tr>
				<th>序号</th>
				<th>保/批单号</th>
				<th>业务关系代码</th>
				<th>险种（产品线）</th>
				<th>车牌/发动机号</th>
				<th>投保人</th>
				<th>签单日期</th>
				<th>生效日期</th>
				<th>差缺文件清单</th>
				<th>即将到期天数</th>
			</tr>
		</thead>
		<tbody class="nowrap">
		<#list data as row>
			<tr class="<#if (row_index + 1) % 2 == 1>odd<#else>even</#if>">
				<td class="number">${ row_index + 1 }</td>
				<td>${ row[0]!"" }</td>
				<td>${ row[1]!"" }</td>
				<td>${ row[2]!"" }</td>
				<td>${ row[3]!"" }</td>
				<td>${ row[4]!"" }</td>
				<td class="date">${ row[5]!"" }</td>
				<td class="date">${ row[6]!"" }</td>
				<td>${ row[7]!"" }</td>
				<td class="number">${ 0-row[8] }</td>
			</tr>
		</#list>
		</tbody>
	</table>
</p>
</#if>


<#if expired??>
<p>您有${ expired?size }份差缺的资料已超期，目前已无法提交投保单，请尽快补回.</p>
<p>
	<table>
		<thead>
			<tr>
				<th>序号</th>
				<th>保/批单号</th>
				<th>业务关系代码</th>
				<th>险种（产品线）</th>
				<th>车牌/发动机号</th>
				<th>投保人</th>
				<th>签单日期</th>
				<th>生效日期</th>
				<th>差缺文件清单</th>
				<th>超期天数</th>
			</tr>
		</thead>
		<tbody class="nowrap">
		<#list expired as row>
			<tr class="<#if (row_index + 1) % 2 == 1>odd<#else>even</#if>">
				<td class="number">${ row_index + 1 }</td>
				<td>${ row[0]!"" }</td>
				<td>${ row[1]!"" }</td>
				<td>${ row[2]!"" }</td>
				<td>${ row[3]!"" }</td>
				<td>${ row[4]!"" }</td>
				<td class="date">${ row[5]!"" }</td>
				<td class="date">${ row[6]!"" }</td>
				<td>${ row[7]!"" }</td>
				<td class="number">${ row[8] }</td>
			</tr>
		</#list>
		</tbody>
	</table>
</p>
</#if>
</body>
</html>
