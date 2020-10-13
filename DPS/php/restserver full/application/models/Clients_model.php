<?php
class Clients_model extends CI_Model{

	function __construct()
	{
	    // llamada al contructor del modelo
		parent::__construct();
		
	}

	public function getClients($email,$profile)
	{
		return $this->_getClients($email, $profile);
	}

	public function getClient ($client = null) {
		return $this->_getClient ($client);
	}


	public function saveLoc($Client,$Address,$data){

		$this->db->trans_start();
		
		$this->db->where('Direccion',$Address)->where('Cliente',$Client);
		$this->db->update('direcciones',$data);

		$this->db->trans_complete();


		if ($this->db->trans_status() === FALSE) {
		    return false;
		}
		else{
			$query = $this->_getClient($Client);
			return $query;
		}
		
		return false;
	}


	public function save($data) {

		$this->db->trans_start();
		
		$this->db->set($this->_setClient($data))->insert('clientes');
		$lastId = $this->db->insert_id();
		
		// Contact phone
		$this->db->set(array('Cliente' => $lastId,
				             'Tipo' => 'telefono',
				             'Valor' => $data['Phone']))->insert('contactos');
		// Contact mail
		$this->db->set(array('Cliente' => $lastId,
                             'Tipo' => 'Correo',
                             'Valor' =>  $data['Email']))->insert('contactos');

		// Address
        $this->db->set(array('cliente' => $lastId,
                             'codigopostal' => $data['ZipCode'],
                             'colonia' => $data['Neighborhood'],
                             'Municipio' => $data['Delegation'],
                             'Estado' => $data['State'],
                             'Ciudad' => $data['City'],
                             'Calle' => $data['Street']))->insert('direcciones');

		$this->db->trans_complete();


		if ($this->db->trans_status() === FALSE) {
		    return false;
		}
		else{
			$query = $this->_getClient($lastId);
			return $query;
		}
		
		return false;

	}


	public function update($data) {

		$clientId = $data['Client'];
		$DireccionId = $data['Address'];

		$FechaHora = date("Y-m-d H:i:s");

		$value=$this->_setClient($data);

		$this->db->trans_start();
		
		$this->db->set($value)->where('Cliente',$clientId)->update('clientes');
		// Contact phone
		$this->db->set(array('Valor' => $data['Phone'], 
							 'FechaHora' => $FechaHora))
		          ->where('Cliente',$clientId)->where('Tipo','telefono')->update('contactos');
		// Contact mail
		$this->db->set(array('Valor' =>  $data['Email'],
							 'FechaHora' => $FechaHora))
		          ->where('Cliente',$clientId)->where('Tipo','Correo')->update('contactos');

		// Address
        $this->db->set(array('codigopostal' => $data['ZipCode'],
                             'colonia' => $data['Neighborhood'],
                             'Municipio' => $data['Delegation'],
                             'Estado' => $data['State'],
                             'Ciudad' => $data['City'],
                             'Calle' => $data['Street'],
                             'Longitud' => $data['Longitude'],
                             'Latitud' => $data['Latitude'],
                             'LocEditable' => $data['LocEditable'],
                             'FechaHora' => $FechaHora ) )
                  ->where('Cliente',$clientId)->where('Direccion',$DireccionId)->update('direcciones');

		$this->db->trans_complete();


		if ($this->db->trans_status() === FALSE) {
		    return false;
		}
		else {
			$query = $this->_getClient($clientId);
			return $query;
		}
		
		return false;
	}




	public function find($email = null,$profile = null,$search = null,$status = null) {

		$array = array("UPPER(A.rfc)" => $search, 
					   "UPPER(B.calle)" => $search,
					   "UPPER(B.ciudad)" => $search,
					   "UPPER(B.codigoPostal)" => $search,
					   "UPPER(B.direccion)" => $search,
					   "UPPER(B.estado)" => $search,
					   "UPPER(B.municipio)" => $search,
					   "''" => $search);	   

			
		$this->db->select("A.Cliente,A.Rfc,A.Nombre,A.FechaHora,A.CorreoAgente,A.Eliminado,A.Estatus ");
		$this->db->from("clientes A");
		$this->db->join("direcciones B", "A.Cliente=B.Cliente", "left");
		$this->db->group_start();
		$this->db->where("A.CorreoAgente", $email);
		$this->db->or_where("'Administrador'",$profile);
		$this->db->group_end();
		$this->db->group_start();
		$this->db->like('UPPER(A.Nombre)', $search);
        $this->db->or_like($array);
		$this->db->group_end();
		$this->db->where_in("A.Estatus",$status); 
		$this->db->order_by("A.Cliente", "DESC");
	    $this->db->order_by("A.FechaHora", "DESC");
        $this->db->order_by("A.Estatus", "DESC");
        $clients = $this->db->get();


		if($clients->num_rows() > 0) {
			return $clients->result_array();
		}
		
		return false;
				
	} 


	private function _getClients($email = null,$profile = null) {

		if (!(is_null($email) || is_null($profile)) ) {

			$this->db->select('A.Cliente,A.Rfc,A.Nombre,A.FechaHora,A.CorreoAgente,A.Eliminado,A.Estatus ');
			$this->db->from('clientes A');
			$this->db->group_start();
			$this->db->where("A.CorreoAgente", $email);
			$this->db->where("'Administrador'",$profile);
			$this->db->group_end();
			$this->db->order_by("A.Estatus", "DESC");
	        $this->db->order_by("A.FechaHora", "DESC");
	        $clients = $this->db->get();
							  
			return $clients->result_array();
		} 

		return false;
	}


	private function _getClient($client = null) {
		
		if(!is_null($client)) {
			$query = $this->db->select('*')->from('clientes')->where('Cliente',$client)->get();
		
			//if($query->num_rows() > 0){
		//		return $query->row_array();
	//		}

			if ($query->num_rows() > 0) {
				return $query->row();
			}

			//else if ($query->num_rows() > 1) {
			//	return $query->result_array();	
			//}

			return false;
		}
		
		return false;
	}


	private function _setClient($data){

 		if(isset($data['Status'])) {
		    $status = trim($data['Status']);     
		}
		else{
		    $status = '';
		}

		return array('Rfc' => $data['Rfc'],
		             'Nombre' => $data['Name'],
			     	 'CorreoAgente' => $data['EmailAgent'],
			     	 'Estatus' => $status,
			     	 'FechaHora' => date("Y-m-d H:i:s")
					);
	} 

}