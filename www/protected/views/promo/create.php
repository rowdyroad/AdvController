<?php
$this->pageTitle = 'Загрузка ролика';

$this->breadcrumbs=array(
	'Ролики'=>array('index'),
	'Загрузить',
);

$this->menu=array(
	array('label'=>'Все ролики', 'url'=>array('index'))
);
?>

<h1><?= $this->pageTitle ?></h1>

<?php echo $this->renderPartial('_form', array('model'=>$model, 'action'=>'create')); ?>