using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class attack_trigger : MonoBehaviour {

	void OnTriggerEnter(Collider col){
		if (col.CompareTag ("mummy") ) {
			Debug.Log (col.gameObject.tag);
			Debug.Log ("player: fuck u");
			Destroy (col.gameObject);
		}
	}
}
