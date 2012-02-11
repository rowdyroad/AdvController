<?php

    $this->pageTitle = 'Добавление проектора';
    
    $cinema_id = isset($_REQUEST['id']) ? intval($_REQUEST['id']) : 0;
    
    if ($cinema_id == 0)
    {
        $this->breadcrumbs = array('Проекторы' => array('index'), 'Добавление проектора');    
    }
    else
    {
        $obj = Yii::app()
                ->db
                ->createCommand("select 
                                        c.*,
                                        n.name as network_name 
                                        from 
                                            cinemas as c, 
                                            networks as n 
                                        where 
                                            c.cinema_id = :cinema_id 
                                            and
                                            c.network_id = n.network_id
                                        ")
                ->bindParam(":cinema_id",$cinema_id)
                ->queryRow();
            
        $this->breadcrumbs=array('Сети кинотеатров'=>array('network/index'),
            $obj['network_name']=>array('network/view',"id"=>$obj['network_id']),
        	$obj['name']=>array('network/viewCinema','id'=>$obj['cinema_id']),
            'Добавление проектора'
        );
    
    }
    
    $this->menu = array(array('label' => 'Все проекторы', 'url' => array('index')));
    
    ?>
    
    <h1><?=
    
    $this->pageTitle
    
    ?></h1>
    
    <?php
    
    echo $this->renderPartial('_form', array('can_modify_pk' => true, 'model' => $model,
        'cinema_id' => $cinema_id));

?>