<a href="<?=$this->createUrl('create');?>">Добавить</a>
<?    
    $data = new CActiveDataProvider('VFake');
    $this->widget('zii.widgets.grid.CGridView', array(
	'id'=>'fake-grid',
	'dataProvider'=>$data,
	'columns'=>
	array(
		array(
            'name'=>'cinema',
            'value'=>'$data[\'cinema\']." ".$data[\'cinema_location\']." Зал:".$data[\'cinema_hall\']'           
        ),
       	array(
            'name'=>'fake',
            'value'=>'$data[\'fake_cinema\']." ".$data[\'fake_cinema_location\']." Зал:".$data[\'fake_cinema_hall\']'
        ),
        array('class'=>'CButtonColumn',"template"=>"{delete}")
   )));
 ?>