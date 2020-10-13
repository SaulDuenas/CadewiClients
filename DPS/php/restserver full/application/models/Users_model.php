<?php
class Users_model extends CI_Model{

	function __construct()
	{
	    // llamada al contructor del modelo
		parent::__construct();
		
	}

	public function get($email = null, $status= null)
	{
		
		return $this->_get($email,$status);
	}



	public function save($data) {

		$email = $data['Email'];
		$profile = $data['Profile'];

		$this->db->trans_start();
		// Add user
		$this->db->set($this->_setUser($data))->insert('usuarios');
		// add Perfil
		$this->db->set(array('Perfil' => $profile,'Correo' => $email))->insert('usuariosperfiles');
		// close transaction
		$this->db->trans_complete();


		if ($this->db->trans_status() === FALSE) {
		    return false;
		}
		else{
			$user = $this->_get($email);
			return $user;
		}
		
		return false;

	}


	public function update($data) {

		$email = trim($data['Email']);
		$profile = trim($data['Profile']);

		$this->db->trans_start();
		$this->db->set($this->_setUser($data))->where('Correo',$email)->update('usuarios');
		// update Perfil
		$this->db->set(array('Perfil' => $profile))->where('Correo',$email)->update('usuariosperfiles');
		// close transaction
		$this->db->trans_complete();

		if ($this->db->trans_status() === FALSE) {
				
			return null;
		}
		else {

			$query = $this->_get($email);
			
			return $query;
			
		}

	}


	public function delete($mail){
		$this->db->where('Correo',$mail)->delete('usuarios');

		if ($this->db->affected_rows() === 1){
			return true;
		}

		return false;
	}


	private function _setUser($data){
		
		  if(isset($data['Status'])) {
		       $status = trim($data['Status']);     
		    }
		    else{
		        $status = '';
		    }


		return array(
			'Correo' => $data['Email'],
			'Nombre' => $data['Name'],
			'Apellidos' => $data['Last_Name'],
			'Password' => $data['Passw'],
			'Estatus' => $status,
			'FechaHora' => date("Y-m-d H:i:s")
		);
	} 


	private function _get($pEmail = null, $pStatus= null) {
	
		$this->db->select("A.Correo,A.Nombre,A.Apellidos,A.Password,IFNULL(A.SesionKey,'') as SesionKey,A.FechaHora,A.Eliminado,IFNULL(A.Usuario,'') as Usuario,B.Perfil, A.Estatus");
		$this->db->from ("usuarios A"); 
		$this->db->join("usuariosperfiles B", "A.Correo=B.Correo", "left");
	//	$this->db->where("A.Correo", $email);
	//	$this->db->or_where("''",$email);	
		if (!is_null($pEmail))  { $this->db->where("A.Correo", $pEmail);  }
		if (!is_null($pStatus)) { $this->db->where_in("A.Estatus",$pStatus);  }
		$this->db->order_by("A.Estatus", "DESC");
		$this->db->order_by("A.FechaHora", "DESC");
		$query = $this->db->get();

		if(!empty($query->result())) {
			
			if ($query->num_rows() > 0) {
				return $query->row();
			}

			else if ($query->num_rows() > 1) {
				return $query->result_array();	
			}

		}

		return false;
		
	} 


	public function find($search = null, $pStatus= null) {

		$array = array("UPPER(A.Nombre)" => $search, 
					   "UPPER(A.Apellidos)"=> $search,
					   "''" => $search);

         $this->db->select("A.Correo,A.Nombre,A.Apellidos,A.Password,IFNULL(A.SesionKey,'') as SesionKey,A.FechaHora,A.Eliminado,IFNULL(A.Usuario,'') as Usuario,B.Perfil, A.Estatus");
        $this->db->from ("usuarios A"); 
        $this->db->join("usuariosperfiles B", "A.Correo=B.Correo", "left");
        if (!is_null($pStatus)) { $this->db->where_in("A.Estatus",$pStatus);  }
        
       
        if (!is_null($search))  { 
          $this->db->group_start();
          $this->db->like('UPPER(A.Correo)', $search); 
          $this->db->or_like($array);
          $this->db->group_end(); 
      	}
      	
        $this->db->order_by("A.Estatus", "DESC");
        $this->db->order_by("A.FechaHora", "DESC");
        $users = $this->db->get();  

   		return $users->result_array();
		
	}

}