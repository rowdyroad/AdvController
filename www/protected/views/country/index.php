<?php
$this->pageTitle = 'Страны';

$this->breadcrumbs=array(
	'Страны'
);

$this->menu=array(
	array('label'=>'Добавить', 'url'=>array('create'), 'visible' => Yii::app()->user->is_admin)
);


?>

<h1><?= $this->pageTitle ?></h1>

<?php
$gridButtons = array('class'=>'CButtonColumn');

if ( !Yii::app()->user->is_admin )
	$gridButtons['template'] = '{view}';

    $data = new CActiveDataProvider('Country',array('criteria'=>Array('order'=>'name')));
    $this->widget('zii.widgets.grid.CGridView', array(
	'id'=>'country-grid',
	'dataProvider'=>$data,
	'columns'=>array(
		'name',
		$gridButtons
	),
));
?>

