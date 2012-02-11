<?php
$this->pageTitle = 'Сети кинотеатров';

$this->breadcrumbs=array(
	'Сети кинотеатров'
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


    $data = new CActiveDataProvider('Network',array('criteria'=>Array('order'=>'name')));
    $this->widget('zii.widgets.grid.CGridView', array(
	'id'=>'promo-grid',
	'dataProvider'=>$data,
	'columns'=>array(
		'name',
		$gridButtons
	),
));
?>

