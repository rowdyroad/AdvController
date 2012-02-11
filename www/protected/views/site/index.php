<?php $this->pageTitle = 'Главная'; ?>

<h1>Добро пожаловать в <i>&quot;<?php echo CHtml::encode(Yii::app()->name); ?>&quot;</i></h1>

<h3>Статистика</h3>
<table style="width: 50%;">
    <tr>
        <td>Определений</td><td><?=Yii::app()->db->createCommand("select count(*) from events")->queryScalar();?> 
        / <?=Yii::app()->db->createCommand("select count(*) from events where dt_registered > (NOW() - INTERVAL 24 HOUR)")->queryScalar();?></td>
    </tr>
    <tr>
        <td>Роликов в базе</td><td><?=Yii::app()->db->createCommand("select count(*) from promo_idents")->queryScalar();?></td>
    </tr>
    <tr>
        <td>Кинотеатров</td><td><?=Yii::app()->db->createCommand("select count(*) from cinemas")->queryScalar();?></td>
    </tr>
    <tr>
        <td>Проекторов (залов)</td><td><?=Yii::app()->db->createCommand("select count(*) from soas")->queryScalar();?></td>
    </tr>
</table>