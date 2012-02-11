<div class="form">

<?php $form=$this->beginWidget('CActiveForm', array(
	'id'=>'soas-form',
	'enableAjaxValidation'=>false,
)); ?>
	<p class="note">Поля, отмеченные <span class="required">*</span>, обязательны для заполнения.</p>    
    <?
        $ar = 	array( '' => '- выберите кинотеатр -' );         
        $cinemas = CinemaView::model()->findAll();
        foreach ($cinemas as $cinema)
        {
            $ar[$cinema->cinema_id] = sprintf("%s / %s",$cinema->location, $cinema->cinema);
        }
    ?>
	<?php echo $form->errorSummary($model); ?>
    
    <div class="row">
		<?php echo $form->labelEx($model,'soas_id'); ?>
	   <? if (isset($can_modify_pk))  { ?>
    	<?php echo $form->textField($model,'soas_id',array('size'=>60,'maxlength'=>255)); ?>
		<?php echo $form->error($model,'soas_id'); ?>
	   <? } else { ?>
        <div><?=$model->soas_id;?></div>           
        <? } ?>
    </div>
    


	<div class="row">
        <?php echo $form->labelEx($model,'cinema_id'); ?>
        <?php 
        
        if (isset($cinema_id))
        {
            $model->cinema_id = $cinema_id;
        }
        echo $form->dropDownList($model,'cinema_id',$ar); ?>
		<?php echo $form->error($model,'cinema_id'); ?>
    </div>
    
    
	<div class="row">
		<?php echo $form->labelEx($model,'hall'); ?>
		<?php echo $form->textField($model,'hall',array('size'=>60,'maxlength'=>255)); ?>
		<?php echo $form->error($model,'hall'); ?>
	</div>

    <div class="row">
		<?php echo $form->labelEx($model,'annotation'); ?>
		<?php echo $form->textArea($model,'annotation',array('rows'=>6, 'cols'=>50)); ?>
		<?php echo $form->error($model,'annotation'); ?>
	</div>

	<div class="row buttons">
		<?php echo CHtml::submitButton($model->isNewRecord ? 'Создать' : 'Сохранить'); ?>
	</div>

<?php $this->endWidget(); ?>

</div><!-- form -->