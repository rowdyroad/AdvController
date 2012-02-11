<?

class FilteredEvent extends  Event
{
    public static function model($class = __CLASS__)
    {
        return parent::model($class);
    }

    public function tableName()
    {
//        return 'v_filtered_events';
      //  return 'v_max_filtered_events';
      return 'v_middle_filtered_events';
    }
    
    public function attributeLabels()
	{
		$data = parent::attributeLabels();        
        unset($data['probability']);        
        return $data;        
    }    
}

?>