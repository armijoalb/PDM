using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class attack_trigger : MonoBehaviour {

	public ScoreManager score_manager;

	void OnTriggerEnter(Collider col){
		if (col.CompareTag ("mummy")) {
			Debug.Log (col.gameObject.tag);
			Debug.Log ("player: fuck u");
			Destroy (col.gameObject);
			score_manager.updateScore (score_manager.score + 1);
		} else if (col.CompareTag ("big_mummy")) {
			Debug.Log (col.gameObject.tag);
			var big = col.gameObject.GetComponent<mummy_health> ();
			big.looseHealth (1);
			score_manager.updateScore (score_manager.score + 1);
		}
	}
}
