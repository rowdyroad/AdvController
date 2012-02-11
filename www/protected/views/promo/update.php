<?php
$this->pageTitle = 'Изменить ролик "'.$model->name.'"';

$this->breadcrumbs=array(
	'Ролики'=>array('index'),
	$model->name=>array('view','id'=>$model->promo_id),
	'Изменить',
);

$this->menu=array(
	array('label'=>'Все ролики', 'url'=>array('index')),
	array('label'=>'Загрузить', 'url'=>array('create')),
	array('label'=>'Просмотр', 'url'=>array('view', 'id'=>$model->promo_id))
);
?>

<h1>Изменить ролик &quot;<?php echo $model->name; ?>&quot;</h1>

<?php echo $this->renderPartial('_form', array('model'=>$model, 'action'=>'update')); ?>