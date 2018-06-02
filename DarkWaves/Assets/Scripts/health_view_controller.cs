using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class health_view_controller : MonoBehaviour {

	private float text_health = 100f;
	private Text displayed;
	public Health_controller player_health;
	// Use this for initialization
	void Start () {
		displayed = GetComponentInChildren<Text> ();
	}

	void Update(){
		if (text_health != player_health.getCurrentHealth ()) {
			setNewTextHealth (player_health.getCurrentHealth ());
		}
	}

	public float getCurrentDisplayedHealth(){
		return text_health;
	}

	public void setNewTextHealth(float new_health){
		text_health = new_health;
		Debug.Log (text_health);
		displayed.text = text_health.ToString();
	}
	

}
