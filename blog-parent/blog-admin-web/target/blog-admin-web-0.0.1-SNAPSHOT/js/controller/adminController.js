 //控制层 
app.controller('adminController' ,function($scope,$controller   ,adminService){	
	
	$controller('baseController',{$scope:$scope});// 继承
	
    // 读取列表数据绑定到表单中
	$scope.findAll=function(){
		adminService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	// 分页
	$scope.findPage=function(page,rows){			
		adminService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;// 更新总记录数
			}			
		);
	}
	
	// 查询实体
	$scope.findOne=function(id){				
		adminService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	// 查询实体
	$scope.findOneByAccount=function(account){				
		adminService.findOneByAccount(account).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	// 保存
	$scope.save=function(){				
		var serviceObject;// 服务层对象
		if($scope.entity.id!=null){// 如果有ID
			serviceObject=adminService.update( $scope.entity ); // 修改
		}else{
			serviceObject=adminService.add( $scope.entity  );// 增加
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					// 重新查询
		        	$scope.reloadList();// 重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	// 批量删除
	$scope.dele=function(){			
		// 获取选中的复选框
		adminService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();// 刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};// 定义搜索对象
	
	// 搜索
	$scope.search=function(page,rows){			
		adminService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;// 更新总记录数
			}			
		);
	}
	$scope.registerEntity={};//定义注册对象
	// 保存
	$scope.add=function(){		
		adminService.add( $scope.registerEntity).success(
			function(response){
				if(response.success){
					// 如果注册成功跳转到登录页
					location.href="login.html";
					alert("注册成功，请登录！");
				}else{
					alert("注册失败");
				}
			}		
		);				
	}
});	
