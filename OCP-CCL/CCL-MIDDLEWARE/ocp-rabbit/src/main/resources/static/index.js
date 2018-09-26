// 定义举报列表对象
var pointList = function() {
	// 举报列表
	var handlePointList = function() {
		var jqGrid = $("#pointList");
		var parmas = "?variable=&name=&version=&w=&org=&ay=&majoray=&doctype=";
		jqGrid
				.jqGrid(
						{
							caption : "信息点列表", // 标题
							url : "/api/infopoints/all" + parmas, // 请求的url
							mtype : "GET", // 请求方式
							styleUI : "Bootstrap",// 设置jqgrid的全局样式为bootstrap样式
							datatype : "json",
							jsonReader : {
								root : "data.list",// array或者List数据
								page : "data.page",// 当前页码
								total : "data.total",// 总页数
								records : "data.records",// 总记录数
								repeatitems : false
							},
							colNames : [ "操作", 'ID', '版本', '信息点变量', '信息点名',
									'组织机构', '文书类型', '案由', '案由大类', "数据类型",
									"取值范围", "jsonPath", "备注", "只读" ],
							colModel : [ // 这里的name的值必须和pojo中的属性对应
									{
										name : 'w',
										index : 'w',
										width : 20,
										sortable : false,
										hidden : false,
										formatter : function(cellvalue,
												options, rowObject) {
											if (cellvalue == "yes") {
												return "<input type=\"checkbox\" name=\"execute\"/>";
											} else {
												return "";
											}
										}
									},
									{
										name : 'id',
										index : 'id',
										width : 15,
										sortable : false,
										hidden : true
									},
									{
										name : 'version',
										index : 'version',
										width : 100,
										hidden : false
									},
									{
										name : 'variable',
										index : 'variable',
										width : 150,
										sortable : false
									},
									{
										name : 'name',
										index : 'name',
										width : 150,
										sortable : false
									},
									{
										name : 'org',
										index : 'org',
										width : 150,
										sortable : false
									},
									{
										name : 'doctype',
										index : 'doctype',
										width : 150,
										sortable : false
									},
									{
										name : 'ay',
										index : 'ay',
										width : 100,
										sortable : false
									},
									{
										name : 'majoray',
										index : 'majoray',
										width : 100,
										sortable : false,
										hidden : false
									},
									{
										name : 'returntype',
										index : 'returntype',
										width : 100,
										sortable : false,
										hidden : false
									},
									{
										name : 'valuedomain',
										index : 'valuedomain',
										width : 100,
										sortable : false,
										hidden : false
									},
									{
										name : 'jsonpath',
										index : 'jsonpath',
										width : 150,
										sortable : false,
										hidden : true
									},
									{
										name : 'remark',
										index : 'remark',
										width : 25,
										sortable : false,
										hidden : true
									},
									{
										name : 'w',
										index : 'w',
										width : 10,
										sortable : false,
										hidden : true,
										formatter : function(cellvalue,
												options, rowObject) {
											if (cellvalue == "yes") {
												return "否";
											} else {
												return "是";
											}
										}
									} ],
							viewrecords : true, // 定义是否要显示总记录数
							rowNum : 500, // 在grid上显示记录条数(每一页显示的记录数)，这个参数是要被传递到后台
							// (需要需要的是，后台对应的参数名是rows 而不是 pageSize)
							rownumbers : true, // 如果为ture则会在表格左边新增一列，显示行顺序号，从1开始递增。此列名为'rn'
							autowidth : true, // 如果为ture时，则当表格在首次被创建时会根据父元素比例重新调整表格宽度。如果父元素宽度改变，为了使表格宽度能够自动调整则需要实现函数：setGridWidth
							height : 500, // 表格高度，可以是数字，像素值或者百分比
							rownumWidth : 36, // 如果rownumbers为true，则可以设置行号 的宽度
							pager : "#pointListPager", // 分页控件的id
							subGrid : false
						// 是否启用子表格
						}).navGrid('#pointListPager', {
					edit : false,
					add : false,
					del : false,
					search : true
				});
		// 随着窗口的变化，设置jqgrid的宽度
		$(window).bind('resize', function() {
			var width = $('.pointList_wrapper').width() * 0.99;
			jqGrid.setGridWidth(width);
		});
		// 不显示水平滚动条
		jqGrid.closest(".ui-jqgrid-bdiv").css({
			"overflow-x" : "hidden"
		});
		// 条件查询所有用户列表
		$("#searchUserListButton").click(
				function() {
					var searchUsersListForm = $("#searchUserListForm");
					jqGrid.jqGrid().setGridParam(
							{
								page : 1, // 第几页
								url : "/video/user/queryUser?"
										+ searchUsersListForm.serialize(),
							}).trigger("reloadGrid");
				});
	};
	return {
		// 初始化各个函数及对象
		init : function() {
			handlePointList();
		}

	};

}();

jQuery(document).ready(function() {
	pointList.init();
});