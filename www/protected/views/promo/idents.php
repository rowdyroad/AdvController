<?php
$this->pageTitle = 'Ролики';

$this->breadcrumbs=array($this->pageTitle);
?>
<h1><?= $this->pageTitle ?></h1>
<script>
    jQuery('#promo-grid a.deactivate').live('click',function() {
    	if(!confirm('Вы действительно хотите деактивировать ролик?')) return false;
    	$.fn.yiiGridView.update('promo-grid', {
    		type:'POST',
    		url:$(this).attr('href'),
    		success:function() {
    			$.fn.yiiGridView.update('promo-grid');
    		}
    	});
    	return false;
    });
    
    jQuery('#promo-grid a.activate').live('click',function() {
    	if(!confirm('Вы действительно хотите активировать текущий ролик? Внимание! Ролики имеющие такой же идентификатор будут автоматически деактивированы!')) return false;
    	$.fn.yiiGridView.update('promo-grid', {
    		type:'POST',
    		url:$(this).attr('href'),
    		success:function() {
    			$.fn.yiiGridView.update('promo-grid');
    		}
    	});
    	return false;
    });
   
   
    function repeat(func,filename) 
    {
        return " <a href=\"javascript:void(0)\" onclick=\""+func+"('"+filename+"')\">Повторить</a>";
    }
    
    function markPromo(filename)
    {
        var cell = $("span.qq-upload-failed-text:last").css('display','inline');
        cell.html('<img src="/images/loading.gif"/>&nbsp;Идет установка метки на файл...');
        $.getJSON("<?=$this->createUrl("mark");?>",{filename:filename},function(data) {               
            if (data.result == "success") {
                var res = data.data;                
                $.getJSON("<?=$this->createUrl("check");?>",{filename:res.filename},function(data) {               
                    if (data.result == "success" && data.data.ident == res.ident) {                        
                        $.getJSON("<?=$this->createUrl("add");?>",{filename:res.filename, ident:res.ident,name:res.name,length:res.length,hash:res.hash},function(data) {
                            if (data.result == "success") {
                                cell.html('Метка <b>' + res.ident +'</b> была успешна установлена. Ролик <b>'+res.name+'</b> добавлен в базу. <a href="'+res.filename+'">Скачать файл</a>');
                            }                                                        
                        });
                        return;
                    } else {
                        cell.html('Ошибка при установке метки. '+repeat('markPromo',res.filename));
                    }
                    cell.append(repeat('markPromo',res.filename));                    
              });                
            } else {
                cell.html('Ошибка: '+data.error+'. '+repeat('markPromo',filename));
            }
        });      
    }
   
    function checkPromo(filename)
    {
        var cell = $("span.qq-upload-failed-text:last").css('display','inline');
        
        cell.html('<img src="/images/loading.gif"/>&nbsp;Идет проверка файла на наличие метки... ');        
        $.getJSON("<?=$this->createUrl("check");?>",{filename:filename},function(data) {               
            if (data.result == "success") { 
                if (data.data.mark) {                    
                    cell.html('Обнаружена метка <b>'+ data.data.mark + '</b>.');                    
                    if (data.data.promo) { 
                        cell.append(' Ролик <b>' + data.data.promo + '</b>.');
                    } else { 
                        cell.append(' Ролик не обнаружен.');
                    }
                } else {
                    cell.html('Метка не обнаружена. <a href="javascript:void(0)" onclick="markPromo(\''+filename+'\')">Установить метку</a>');
                }
            } else {
                cell.html('Ошибка: '+data.error+'.');
            }
            cell.append(repeat('checkPromo',filename));
        });
    }
</script>
<?
$gridButtons = array('class'=>'CButtonColumn', 
"buttons"=>array(
    "activate"=>array(
	"label"=>"Актив.",
    "options"=>array("class"=>"activate"),
	"url"=>'Yii::app()->createUrl("promo/activate",array("id"=>$data->id))',
	"visible"=>'$data->actived == 0;'
    ),
    "deactivate"=>array(
	"label"=>"Деакив.",
    "options"=>array("class"=>"deactivate"),
	"url"=>'Yii::app()->createUrl("promo/deactivate",array("id"=>$data->id))',
	"visible"=>'$data->actived != 0;'
    ),
    

));


if ( !Yii::app()->user->is_admin ) {
	$gridButtons['template'] = '';
} else {
    $gridButtons['template'] = '{activate}{deactivate}{delete}';
}
    
/*$this->widget('ext.EAjaxUpload.EAjaxUpload',
array(
        'id'=>'uploadFile',
        'config'=>array(
               'action'=>'index.php?r=/promo/upload',
               'debug'=>true,
               'allowedExtensions'=>array("wav"),//array("jpg","jpeg","gif","exe","mov" and etc...
               'sizeLimit'=>100*1024*1024*1024,// maximum file size in bytes
               'minSizeLimit'=>200 * 1024,// minimum file size in bytes
               'onComplete'=>"js:function(id, fileName, responseJSON){ checkPromo('upload/'+fileName); }",
               'messages'=>array(
                                 'typeError'=>"{file} имеет неверно расширение. Допускаются только {extensions}-файлы.",
                                 'sizeError'=>"{file} слишком большой. Максимальный размер файла -  {sizeLimit}.",
                                 'minSizeError'=>"Размер файла {file} слишком мал. Минимальный размер - {minSizeLimit}.",
                                 'emptyError'=>"{file} не содержит данных. Повторите загрузку файла.",
                                 'onLeave'=>"Внимание! Если Вы закроете данную страницу, процесс загрузки будет прерван."
                                ),
               //'showMessage'=>"js:function(message){ alert(message); }"
              )
));*/

$all = intval(Yii::app()->user->is_admin);
$data = new CActiveDataProvider(get_class($model),array(
     'criteria'=>array(
        'condition'=>"(actived=1 or $all = 1)"
    ),
    'pagination'=>false));


$columns = array('ident','name');

if (Yii::app()->user->is_admin) {
    $columns[] =  array(
        'header'=>'Статус',
        'name'=>'actived',
        'value'=>'($data->actived) ? "Активен": "Неактивен"'
        );  
}
       
$columns[] = array(
            'header'=>'Время добавления',
            'name'=>'add_time',
            'value'=>'Utils::DateTimeFromTS(strtotime($data->add_time))'
        );
        
$columns[] = $gridButtons;
    
$this->widget('zii.widgets.grid.CGridView', array(
	'id'=>'promo-grid',
	'dataProvider'=>$data,
    'enablePagination'=>false,
    'rowCssClassExpression'=>'($data->actived == 0) ? "disabled" : (($row % 2 == 0) ? "odd": "even")',
	'columns'=>$columns
)); ?>
