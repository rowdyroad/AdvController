<?php
$this->pageTitle = 'Кинотеатры сети  "'.$model->name.'"';

$this->breadcrumbs=array(
	'Сети кинотеатров'=>array('index'),
	$model->name,
);

$this->menu=array(
	array('label'=>'Добавить кинотеатр', 'url'=>array('network/createCinema', 'id'=>$model->network_id), 'visible' => Yii::app()->user->is_admin),
);
?>

<h1><?= $this->pageTitle ?></h1>
<?
$dp = new CActiveDataProvider(get_class($model),array(
	'pagination' => array(
		'pageSize'=> 100
	),
    'sort'=>array('defaultOrder'=>'last_heart_beat DESC'),
));
$data = new CActiveDataProvider('CinemaView',array('pagination' => array(
		'pageSize'=> 100
	),'criteria'=>Array('condition'=>"`network_id`='$model->network_id'",'order'=>'cinema')));
    $this->widget('zii.widgets.grid.CGridView', array(  
	'id'=>'cinema-grid',
	'dataProvider'=>$data,
	'columns'=>array(
		'cinema',
        'location',
		 array(     'class'=>'CButtonColumn',
                    'buttons'=>array(
                        'delete'=>array('url'=>'Yii::app()->createUrl("network/deleteCinema",array("id"=>$data->cinema_id))'),
                        'update'=>array('url'=>'Yii::app()->createUrl("network/updateCinema",array("id"=>$data->cinema_id))'),
                        'view'=>array('url'=>'Yii::app()->createUrl("network/viewCinema",array("id"=>$data->cinema_id))')
                                          
                    )
              )
	),
));

?>