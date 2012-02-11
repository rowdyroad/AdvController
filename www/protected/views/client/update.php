<?php
$this->pageTitle = 'Правка клиента "'.$model->name.'"';

$this->breadcrumbs=array(
	'Клиенты'=>array('index'),
	$model->name=>array('view','id'=>$model->client_id),
	'Правка',
);

$this->menu=array(
	array('label'=>'Все клиенты', 'url'=>array('index')),
	array('label'=>'Добавить', 'url'=>array('create')),
);
?>
<h1><?=$this->pageTitle;?></h1>
<?php echo $this->renderPartial('_form', array('model'=>$model)); ?>