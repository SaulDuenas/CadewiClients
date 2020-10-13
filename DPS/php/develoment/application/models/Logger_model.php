<?php
class Logger_model extends CI_Model{

	function __construct()
	{
	    // llamada al contructor del modelo
		parent::__construct();
		
	}


	public function search($search = null)
	{
		
		return $this->_search($search);
	}

	public function get($id = null)
	{
		
		return $this->_get($id);
	}


	public function save($data){
		// Add Log
		$this->db->set($data)->insert('logger');	

		$lastId =  null;
		$lastId = $this->db->insert_id();

		if ($lastId != null) {
		    $query = $this->_get($lastId);
			return $query;
		}
				
		return false;
	}


	private function _search($search = null) {
		
		$sql = "SELECT A.log,A.Correo,A.Tipo,A.Categoria,A.Dispositivo,A.Mensaje,A.FechaHora " . 
		       "FROM logger A " .
 		       "WHERE ( UPPER(A.Mensaje) LIKE CONCAT('%',UPPER(?),'%') OR UPPER(?) = '' ) ".
		       "ORDER BY A.FechaHora DESC";

        	$lsearch = '';

		if (is_null($search) ) {
			$lsearch = ''; 
		} elseif (trim($search) === '') {
			$lsearch = '';
		} else {
			$lsearch =trim($search);
		}      

		
		$query = $this->db->query($sql,array($lsearch,$lsearch));

		return $query->result_array();
		
	} 


	private function _get($Id = null) {
		
		$sql = "SELECT A.log,A.Correo,A.Tipo,A.Categoria,A.Dispositivo,A.Mensaje,A.FechaHora " . 
		       "FROM logger A " .
 		       "WHERE ( (A.log = ?) OR (0 = ?) ) ".
		       "ORDER BY A.FechaHora DESC";

        $lId = '-1';

		if (is_null($Id) ) {
			$lId = '-1'; 
		} elseif ($Id === '') {
			$lId = '-1';
		} else {
			$lId =$Id;
		}      
		
		$query = $this->db->query($sql,array($lId,$lId));

		return $query->result_array();
		
	} 

}
