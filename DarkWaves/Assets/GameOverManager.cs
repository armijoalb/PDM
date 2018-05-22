using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class GameOverManager : MonoBehaviour {
	public Health_controller player_health;
	public float restartDelay = 5f;
	Animator anim;
	float restartTimer;
	// Use this for initialization
	void Awake(){
		anim = GetComponent<Animator> ();
	}
	
	// Update is called once per frame
	void Update () {
		if (player_health.getCurrentHealth() <= 0) {
			anim.SetTrigger ("GameOver");
			restartTimer += Time.deltaTime;
			if (restartTimer >= restartDelay) {
				Application.LoadLevel (Application.loadedLevel);
			}
		}
	}
}
