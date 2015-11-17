<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/common/base.jsp"%>
<div>
hello world!
${userName.name}
</div>
<script type="text/javascript">
	seajs.use('${jsBasePath}/example/example.js');
</script>