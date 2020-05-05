//服务层
app.service('blogService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../blog/findAll.do');		
	}
	//分页 
	this.findPage=function(page,rows){
		return $http.get('../blog/findPage.do?page='+page+'&rows='+rows);
	}
	//查询实体
	this.findOne=function(id){
		return $http.get('../blog/findOne.do?id='+id);
	}
	//查询实体
	this.findOneByAccount=function(account){
		return $http.get('../blog/findOne.do?account='+account);
	}
	//增加 
	this.add=function(entity){
		return  $http.post('../blog/add.do',entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../blog/update.do',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../blog/delete.do?ids='+ids);
	}
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../blog/search.do?page='+page+"&rows="+rows, searchEntity);
	}    
	
});
