<?php
$this->pageTitle = 'Изменить кинотеатр "'.$model->name.'"';

$this->breadcrumbs=array(
	'Кинотеатры'=>array('index'),
	$model->name=>array('view','id'=>$model->cinema_id),
	'Изменить',
);

$this->menu=array(
	array('label'=>'Все кинотеатры', 'url'=>array('index')),
	array('label'=>'Создать', 'url'=>array('create'), 'visible' => Yii::app()->user->is_admin),
	array('label'=>'Просмотр', 'url'=>array('view', 'id'=>$model->cinema_id))
);
?>

<h1>Изменить кинотеатр &quot;<?php echo $model->name; ?>&quot;</h1>

<?php echo $this->renderPartial('_form', array('model'=>$model)); ?>