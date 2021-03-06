<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>收藏管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/cms/cmsCollection/">收藏列表</a></li>
		<shiro:hasPermission name="cms:cmsCollection:edit"><li><a href="${ctx}/cms/cmsCollection/form">收藏添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="cmsCollection" action="${ctx}/cms/cmsCollection/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>模块名称：</label>
				<form:select path="modelName" htmlEscape="false" maxlength="255" class="input-medium">
					<form:option value="">请选择</form:option>
					<form:option value="article">资讯</form:option>
					<form:option value="postMain">帖子</form:option>
				</form:select>
			</li>
			<li><label>用户：</label>
				<sys:treeselect id="createBy" name="createBy.id" value="${cmsCollection.createBy.id}" labelName="createBy.name" labelValue="${cmsCollection.createBy.name}"
								title="用户" url="/sys/user/treeData" cssClass="input-small" allowClear="true"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>实例Id</th>
				<th>实例所属模块</th>
				<th>创建者</th>
				<th>创建时间</th>
				<shiro:hasPermission name="cms:cmsCollection:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="cmsCollection">
			<tr>
				<td>
						${cmsCollection.modelId}
				</td>
				<td>
						${cmsCollection.modelName}
				</td>
				<td>
						${cmsCollection.createBy.name}
				</td>
				<td>
					<fmt:formatDate value="${cmsCollection.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="cms:cmsCollection:edit"><td>
    				<a href="${ctx}/cms/cmsCollection/form?id=${cmsCollection.id}">修改</a>
					<a href="${ctx}/cms/cmsCollection/delete?id=${cmsCollection.id}" onclick="return confirmx('确认要删除该收藏吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>