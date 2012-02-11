<?php

class PromoRegistrationEventController extends Controller
{
    /**
     * @var string the default layout for the views. Defaults to '//layouts/column2', meaning
     * using two-column layout. See 'protected/views/layouts/column2.php'.
     */
    public $layout='//layouts/column2';

    /**
     * @return array action filters
     */
    public function filters()
    {
        return array(
            'accessControl', // perform access control for CRUD operations
        );
    }

    /**
     * Specifies the access control rules.
     * This method is used by the 'accessControl' filter.
     * @return array access control rules
     */
    public function accessRules()
    {
        return array(
            array('allow',  // allow all users to perform 'index' and 'view' actions
                'actions'=>array('index','view'),
                'users'=>array('@'),
            ),
            array('deny',  // deny all users
                'users'=>array('*'),
            ),
        );
    }

    /**
     * Displays a particular model.
     * @param integer $id the ID of the model to be displayed
     */
    public function actionView($id)
    {
        $this->render('view',array(
            'model'=>$this->loadModel($id),
        ));
    }

    /**
     * Lists all models.
     */
    public function actionIndex()
    {        
       /* $row = Yii::app()->db->CreateCommand("select promo_registration_event_id as id, promo_id, soas_id from promo_registration_events where approved = 1 order by promo_registration_event_id asc")->queryAll();
        
        $last = 0;
        $soas_id = 0;
        
        for ($i = 0; $i < count($row); ++$i)
        {
            if ($last != $row[$i]['promo_id'])
            {
                $last = $row[$i]['promo_id'];
                $soas_id = $row[$i]['soas_id'];
                continue;
            }
            else
            {
                if ($row[$i]['soas_id'] == $soas_id)
                {
                  Yii::app()->db->CreateCommand("update promo_registration_events set approved = 0 where promo_registration_event_id = {$row[$i]['id']}")->execute();
                }
            }            
        }
        
        
        
        
        
        $row = Yii::app()->db->CreateCommand("
        select 
        id
        from
        (
        select 
            promo_registration_event_id as id, UNIX_TIMESTAMP(dt_registered) as t,
            (select UNIX_TIMESTAMP(dt_registered) from promo_registration_events as p2 where p2.promo_registration_event_id > p1.promo_registration_event_id and p1.soas_id = p2.soas_id and  p2.approved = 1 order by p2.promo_registration_event_id asc limit 1) as n_time,
            (select UNIX_TIMESTAMP(dt_registered) from promo_registration_events as p3 where p3.promo_registration_event_id < p1.promo_registration_event_id and p1.soas_id = p3.soas_id and  p3.approved = 1 order by p3.promo_registration_event_id desc limit 1 ) as l_time
        from
            promo_registration_events as p1
            where approved = 1        
        ) as rt
        where 
        (t - l_time > 300) and (n_time - t > 300)
         order by id desc
        ")->queryColumn();
        
        //print_r($row);
        
        Yii::app()->db->CreateCommand("update promo_registration_events set approved = 0 where promo_registration_event_id in (".implode(",",$row).")")->execute();
        
       */
        
                
		$this->layout = '//layouts/column1';		
        $model=new PromoRegistrationEvent('search');
        $model->unsetAttributes();  // clear any default values
        if(isset($_GET['PromoRegistrationEvent']))
            $model->attributes=$_GET['PromoRegistrationEvent'];

        $this->render('index',array(
            'model'=>$model,
        ));
    }

    /**
     * Returns the data model based on the primary key given in the GET variable.
     * If the data model is not found, an HTTP exception will be raised.
     * @param integer the ID of the model to be loaded
     */
    public function loadModel($id)
    {
        $model=PromoRegistrationEvent::model()->findByPk((int)$id);
        if($model===null)
            throw new CHttpException(404,'The requested page does not exist.');
        return $model;
    }
}
