 //控制层 
app.controller('blogController' ,function($scope,$controller   ,blogService){	
	
	$controller('baseController',{$scope:$scope});// 继承
	
    // 读取列表数据绑定到表单中
	$scope.findAll=function(){
		blogService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	// 分页
	$scope.findPage=function(page,rows){			
		blogService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;// 更新总记录数
			}			
		);
	}
	
	// 查询实体
	$scope.findOne=function(id){				
		blogService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	// 查询实体
	$scope.findOneByAccount=function(account){				
		blogService.findOneByAccount(account).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	// 保存
	$scope.save=function(){				
		var serviceObject;// 服务层对象
		if($scope.entity.id!=null){// 如果有ID
			serviceObject=blogService.update( $scope.entity ); // 修改
		}else{
			serviceObject=blogService.add( $scope.entity  );// 增加
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
		blogService.dele( $scope.selectIds ).success(
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
		blogService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;// 更新总记录数
			}			
		);
	}
	// 保存
	$scope.add=function(){		
		$scope.entity.content=editor.html();
		blogService.add( $scope.entity).success(
			function(response){
				if(response.success){
					alert("新增成功！");
				}else{
					alert(response.message);
				}
			}		
		);				
	}
});	
