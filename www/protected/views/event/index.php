<?php
$this->pageTitle = 'Журнал событий';

$this->breadcrumbs=array(
	$this->pageTitle
);

function getValuePairs($id_field, $name_field, $from, $order = "", $where = "")
{
    if (empty($order)) $order = $name_field;
    $r = Yii::app()->db->CreateCommand("select `$id_field`, `$name_field` from `$from` $where order by `$order`")->queryAll();
    $data = Array();
    foreach ($r as $row)
    {
        $data[$row[$id_field]] = $row[$name_field];
    }
    return $data;
}
function checkedItem($item, $param_name, $name)
{
    $text = "";
    if (is_array($name))
    {
        foreach ($name as $n)
        {
            $text.=$item[$n]." ";
        }
        $text = trim($text);
    }
    else
    {
        $text = $item[$name];
    }

    $has = (!empty($_REQUEST['EventSearch'][$param_name]) && in_array($item[$param_name],$_REQUEST['EventSearch'][$param_name]));

    return Array(
                'text'=>
                '<input value="'.$item[$param_name].'" name="EventSearch['.$param_name.'][]" '.(($has) ? " checked " : ""). 'type="checkbox" />&nbsp;'.$text,'expanded'=>false,'checked'=>$has);
}


?>
<h1><?= $this->pageTitle ?></h1>
<?
    $date = CHtml::textField(   'EventSearch[dt_registered_begin]',
                                @$_REQUEST['EventSearch']['dt_registered_begin'],
                                array("id"=>"dt_registered_begin")).
            $this->widget(      'application.extensions.calendar.SCalendar',
                                array('inputField'=>'dt_registered_begin','language'=>'ru-UTF'),true).
            CHtml::textField(   'EventSearch[dt_registered_end]',
                                 @$_REQUEST['EventSearch']['dt_registered_end'],array("id"=>"dt_registered_end")).
            $this->widget(      'application.extensions.calendar.SCalendar',array('inputField'=>'dt_registered_end','language'=>'ru-UTF'),true);

    $promos = CHtml::checkBoxList('EventSearch[promo_id]',@$_REQUEST['EventSearch']['promo_id'], getValuePairs('id','name','promo_idents',""," where actived = 1 "));
    $clients = CHtml::checkBoxList('EventSearch[client_ident]',@$_REQUEST['EventSearch']['client_ident'], getValuePairs('client_id','name','clients'));

    $network_expanded = false;
    $networks_children = Array();
    $networks = Yii::app()->db->CreateCommand("select network_id,name from networks order by name")->queryAll();
    foreach ($networks as $network)
    {
	if (!AcUser::hasNetwork($network['network_id'])) {
	    continue;
	}
        $n_data = checkedItem($network,'network_id','name');
        $cinemas = Yii::app()->db->CreateCommand("select cinema_id,name from cinemas where network_id = '".$network['network_id']."' order by name")->queryAll();
        foreach ($cinemas as $cinema)
        {
           $c_data = checkedItem($cinema,'cinema_id','name');
           $soases = Yii::app()->db->CreateCommand("select soas_id,hall,'зал' as name from soas where cinema_id = '".$cinema['cinema_id']."'  order by hall")->queryAll();
           foreach ($soases as $soas)
           {
                $item = checkedItem($soas,'soas_id',array('hall','name'));
                $c_data['children'][] = $item;
                if ($item['checked'])
                {
                    $c_data['expanded'] = true;
                }
           }

            if ($c_data['checked'] || $c_data['expanded'])
            {
                $n_data['expanded'] = true;
            }

           $n_data['children'][] = $c_data;
        }
            if ($n_data['checked'] || $n_data['expanded'])
            {
                $network_expanded = true;
            }


        $networks_children[] = $n_data;
    }

    $locations_expanded = false;
    $locations_children = Array();
    $countries = Yii::app()->db->CreateCommand("select country_id, name from countries   order by name")->queryAll();
    foreach ($countries as $country)
    {

        $n_data = checkedItem($country,'country_id','name');


        $cities = Yii::app()->db->CreateCommand("select city_id,name from cities where country_id = '".$country['country_id']."'   order by name")->queryAll();
        foreach ($cities as $city)
        {
            $item = checkedItem($city, 'city_id','name');
            $n_data['children'][] = $item;
            if ($item['checked'])
            {
                $n_data['expanded'] = true;
            }
        }

        if ( $n_data['checked'] ||  $n_data['expanded'])
        {
            $locations_expanded = true;
        }

        $locations_children[] = $n_data;
    }



    $menu = Array(
        array('text'=>'Дата','expanded' => (!empty($_REQUEST['EventSearch']['dt_registered_begin']) || !empty($_REQUEST['EventSearch']['dt_registered_end'])), 'children'=>Array(array('text'=>$date))),
        array('text'=>'Ролики','expanded' => !empty($_REQUEST['EventSearch']['promo_id']), 'children'=>Array(array('text'=>$promos))),
        array('text'=>'Кинотеатры','expanded' => $network_expanded, 'children'=>$networks_children),
        array('text'=>'Местоположения','expanded' => $locations_expanded, 'children'=>$locations_children),
        array('text'=>'Клиенты','expanded' => !empty($_REQUEST['EventSearch']['client_id']), 'children'=>Array(array('text'=>$clients))),
);
?>
    <div style="border:1px solid silver;padding:4px;">
    <h3>Фильтр</h3>
    <form method="post">
    <?
        $this->widget('CTreeView', array('data' => $menu));
    ?>

    <br />
    <input type="submit" value="Применить фильтр"/>
    </div>
    <div style="width:100%;text-align:right;padding-top:12px">
    <input type="submit" value="Экпорт в Microsoft Excel"/ name="EventSearch[excel]"/>
    </div>
    </form>
<?php

$params = (isset($criteria)) ? array('criteria'=>$criteria) : array();

$data = new CActiveDataProvider(get_class($model),$params);
$data->pagination->pageSize = 100;

if (isset($criteria))
{
    $data->pagination->applyLimit($criteria);
    $data->pagination->params = $_REQUEST;   
}

$columns = Array(
        array(            // display 'create_time' using an expression
            'name'=>'dt_registered',
            'value'=>'Utils::DateTimeFromTS(strtotime($data->dt_registered))',
        ),
        'promo_name',
        'cinema',
        'hall',
        'location',
        'client_name',
        'probability');    

$this->widget('zii.widgets.grid.CGridView', array(
	'id'=>'event-grid',
	'dataProvider'=>$data,
	'columns'=>$columns
)); ?>

<?
    if (!Yii::app()->user->isGuest && Yii::app()->user->is_admin) {
        ?><a href="<?=$this->createUrl('fakes/index');?>">...</a><?
    }
?>
                