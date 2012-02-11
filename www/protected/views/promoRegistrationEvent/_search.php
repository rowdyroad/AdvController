<div class="wide form">

<?php $form=$this->beginWidget('CActiveForm', array(
    'action'=>Yii::app()->createUrl($this->route),
    'method'=>'get',
)); ?>

    <div class="row">
        <?php echo $form->label($model,'dt_registered'); ?>
        <?php echo $form->textField($model,'dt_registered'); ?>
    </div>

    <div class="row">
        <?php echo $form->label($model,'soas_id'); ?>
        <?php echo $form->dropDownList(
						$model,'soas_id',
						array( '' => '- выберите сервер -' ) + CHtml::listData(Soas::model()->findAll(), 'soas_id', 'name')
						
		); ?>
    </div>

    <div class="row">
        <?php echo $form->label($model,'promo_id'); ?>
        <?php echo $form->dropDownList(
						$model,'promo_id',
						array( '' => '- выберите ролик -' ) + CHtml::listData(Promo::model()->findAll(), 'promo_id', 'name')
				); ?>		
    </div>

    <div class="row buttons">
        <?php echo CHtml::submitButton('Поиск'); ?>
    </div>

<?php $this->endWidget(); ?>

</div><!-- search-form -->