<?php

/**
 * Класс предоставляет данные для страницы регистрации роликов.
 * В связи с тем, что этот запрос затруднительно эффективно представить на
 * ActiveRecord, потребовалось написать собственный класс - провайдер данных.
 */
class PromoRegistrationEventListProvider extends CDataProvider
{
	/**
	 * @var PromoRegistrationEvent Объект, содержащий параметры фильтрации.
	 * Допускается использование операндов сравнения (>, <, = и т.п.). 
	 */
	public $filterModel = null;
	
	// Строка WHERE для запроса. Формируется в конструкторе.
	private $whereSQL = '';
	// Параметры запроса. Формируется в конструкторе.
	private $params = array();
	
	public function __construct($config=array())
	{
		$this->setId('PromoRegistrationEventListProvider');
		foreach ($config as $key => $value)
			$this->$key = $value;
		
		$whereFields = array();
		
		if ($this->filterModel !== null)
		{
			$whereFields = array();

			if ( strlen($this->filterModel->dt_registered) > 0 )
			{
				$op = self::parseOperand($this->filterModel->dt_registered);
				$whereFields['dt_registered'] = 'promo_registration_events.dt_registered'.$op['operand'].':dt_registered';
				$this->params[':dt_registered'] = array( $op['value'], PDO::PARAM_STR );
			}
			if ( strlen($this->filterModel->soas_id) > 0 )
			{
				$whereFields['soas_id'] = 'promo_registration_events.soas_id=:soas_id';
				$this->params[':soas_id'] = array( $this->filterModel->soas_id, PDO::PARAM_INT );
			}
			
			if ( strlen($this->filterModel->promo_id) > 0 )
			{
				$whereFields['promo_id'] = 'promo_registration_events.promo_id=:promo_id';
				$this->params[':promo_id'] = array( $this->filterModel->promo_id, PDO::PARAM_INT );
			}
			
            $whereFields[] = 'approved = 1';
			if (count($whereFields)>0)
				$this->whereSQL = 'WHERE '.implode(' AND ', $whereFields);
		}
	}
	
	/**
	 * Функция разбивает строку вида (<2) на операнд и значение 
	 * @param string Значение поля 
	 * @return array Массив ( 'operand' => '</<=/>/>=/<>/=', value => значение параметра)
	 */
	static private function parseOperand($fieldValue)
	{
		$res = array(
			'operand' => '='
		);
		
		if ( preg_match('/([<>=]{1,2})(.*)/', $fieldValue, $matches) === 1 )
		{
			$res['operand'] = $matches[1];
			$res['value'] = $matches[2];
		}
		else
			$res['value'] = $fieldValue;
		
		return $res;
	}

	protected function fetchData()
	{
		$limit = '';
		
		if(($pagination=$this->getPagination())!==false)
		{
			$pagination->setItemCount($this->getTotalItemCount());
			$limit = 'LIMIT '.$pagination->getPageSize().' OFFSET '.($pagination->getPageSize()*$pagination->getCurrentPage());
		}
		
		$sql = "
                
        
			SELECT
				promo_registration_events.promo_registration_event_id,
				promo_registration_events.dt_registered,
				promos.name AS promo_name,                
				soas.hall AS hall,
				CONCAT(networks.name,' ',cinemas.name) AS cinema_name,
                CONCAT(countries.name,' ',cities.name) as location
			FROM promo_registration_events
			INNER JOIN promos ON ( promo_registration_events.promo_id = promos.promo_id ) 
			INNER JOIN soas ON ( promo_registration_events.soas_id = soas.soas_id )             
			LEFT OUTER JOIN cinemas ON ( soas.cinema_id = cinemas.cinema_id )
            INNER JOIN  networks ON ( networks.network_id= cinemas.network_id)
            INNER JOIN  countries ON (cinemas.country_id = countries.country_id)
            INNER JOIN  cities ON (cinemas.city_id = cities.city_id)            
			$this->whereSQL
			ORDER BY promo_registration_events.dt_registered DESC
			$limit";
		$command = Yii::app()->db->createCommand($sql);
		
		foreach ($this->params as $param => $data)
			$command->bindValue($param, $data[0], $data[1]);
				
		return $command->queryAll();
	}
	
	protected function fetchKeys()
	{
		$keys = array();
		foreach ($this->getData() as $i => $data)
			$keys[$i] = $data['promo_registration_event_id'];

		return $keys;
	}

	protected function calculateTotalItemCount()
	{
		$command = Yii::app()->db->createCommand('
			SELECT count(*)
			FROM promo_registration_events
			INNER JOIN promos ON ( promo_registration_events.promo_id = promos.promo_id )
			'.$this->whereSQL);
		
		foreach ($this->params as $param => $data)
			$command->bindValue($param, $data[0], $data[1]);
		
		return intval($command->queryScalar());
	}
}

?>
