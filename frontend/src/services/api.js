import axios from "axios";

const API =
  axios.create({

    baseURL: "https://ai-gitrepo-explainer.onrender.com/api"
  });

export default API;