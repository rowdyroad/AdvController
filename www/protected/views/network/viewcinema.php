<?php

$network =  Network::model()->findByPk($model->network_id);


$this->pageTitle = 'Проекторы кинотеатра  "'.$model->name.'"';

$this->breadcrumbs=array(
	'Сети кинотеатров'=>array('index'),
    $network->name=>array('network/view',"id"=>$model->network_id),
	$model->name
);

$this->menu=array(
	array('label'=>'Добавить проектор', 'url'=>array('soas/create', 'id'=>$model->cinema_id), 'visible' => Yii::app()->user->is_admin),
);
?>

<h1><?= $this->pageTitle ?></h1>
<?

$data = new CActiveDataProvider('Soas',array('pagination' => array(
		'pageSize'=> 100
	), 'criteria'=>Array('condition'=>"`cinema_id`='$model->cinema_id'",'order'=>'hall')));
    $this->widget('zii.widgets.grid.CGridView', array(  
	'id'=>'cinema-grid',
	'dataProvider'=>$data,
	'columns'=>array(
		'hall',        
		'soas_id',
		'last_heart_beat',
		 array(     'class'=>'CButtonColumn',
                    'buttons'=>array(
                        'delete'=>array('url'=>'Yii::app()->createUrl("soas/delete",array("id"=>$data->soas_id))'),
                        'update'=>array('url'=>'Yii::app()->createUrl("soas/update",array("id"=>$data->soas_id))'),
                        'view'=>array('url'=>'Yii::app()->createUrl("soas/view",array("id"=>$data->soas_id))')
                                          
                    )
              )
	),
));

?>